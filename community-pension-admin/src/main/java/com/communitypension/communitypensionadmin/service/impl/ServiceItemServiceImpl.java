package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.dto.ServiceItemDTO;
import com.communitypension.communitypensionadmin.entity.ServiceItem;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.ServiceItemMapper;
import com.communitypension.communitypensionadmin.service.ServiceItemService;
import com.communitypension.communitypensionadmin.vo.ServiceItemVO;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务项目Service实现类
 */
@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {
    /**
     * @param page 分页参数
     * @param serviceItem 查询条件
     * @return
     */
    @Override
    public Page<ServiceItemVO> getServiceItemList(Page<ServiceItem> page, ServiceItem serviceItem) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (serviceItem != null) {
            if (StringUtils.hasText(serviceItem.getServiceName())) {
                wrapper.like(ServiceItem::getServiceName, serviceItem.getServiceName());
            }
            if (StringUtils.hasText(serviceItem.getServiceType())) {
                wrapper.eq(ServiceItem::getServiceType, serviceItem.getServiceType());
            }
            if (serviceItem.getStatus() != null) {
                wrapper.eq(ServiceItem::getStatus, serviceItem.getStatus());
            }
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(ServiceItem::getCreateTime);
        
        // 执行分页查询
        Page<ServiceItem> serviceItemPage = this.page(page, wrapper);
        
        // 转换为VO
        Page<ServiceItemVO> voPage = new Page<>(serviceItemPage.getCurrent(), serviceItemPage.getSize(), serviceItemPage.getTotal());
        voPage.setRecords(serviceItemPage.getRecords().stream().map(this::convertToVO).toList());
        
        return voPage;
    }

    @Override
    public ServiceItemVO getServiceItemById(Long serviceId) {
        ServiceItem serviceItem = this.getById(serviceId);
        return serviceItem != null ? convertToVO(serviceItem) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addServiceItem(ServiceItemDTO serviceItemDTO) {
        ServiceItem serviceItem = new ServiceItem();
        BeanUtils.copyProperties(serviceItemDTO, serviceItem);
        return this.save(serviceItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateServiceItem(ServiceItemDTO serviceItemDTO) {
        ServiceItem serviceItem = new ServiceItem();
        BeanUtils.copyProperties(serviceItemDTO, serviceItem);
        return this.updateById(serviceItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteServiceItem(Long serviceId) {
        return this.removeById(serviceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteServiceItems(List<Long> serviceIds) {
        return this.removeByIds(serviceIds);
    }

    @Override
    public void exportServiceItems(HttpServletResponse response, ServiceItem serviceItem) {
        try {
            // 创建查询条件
            LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
            if (serviceItem != null) {
                if (StringUtils.hasText(serviceItem.getServiceName())) {
                    wrapper.like(ServiceItem::getServiceName, serviceItem.getServiceName());
                }
                if (StringUtils.hasText(serviceItem.getServiceType())) {
                    wrapper.eq(ServiceItem::getServiceType, serviceItem.getServiceType());
                }
                if (serviceItem.getStatus() != null) {
                    wrapper.eq(ServiceItem::getStatus, serviceItem.getStatus());
                }
            }
            wrapper.orderByDesc(ServiceItem::getCreateTime);
            
            // 查询数据
            List<ServiceItem> list = this.list(wrapper);
            
            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("服务项目列表");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"服务项目ID", "服务名称", "服务类型", "服务描述", "服务价格", "服务时长", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据
            int rowNum = 1;
            for (ServiceItem item : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getServiceId());
                row.createCell(1).setCellValue(item.getServiceName());
                row.createCell(2).setCellValue(item.getServiceType());
                row.createCell(3).setCellValue(item.getDescription());
                row.createCell(4).setCellValue(item.getPrice().doubleValue());
                row.createCell(5).setCellValue(item.getDuration());
                row.createCell(6).setCellValue("0".equals(item.getStatus()) ? "正常" : "停用");
                row.createCell(7).setCellValue(item.getCreateTime().toString());
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + 
                URLEncoder.encode("服务项目列表.xlsx", StandardCharsets.UTF_8));
            
            // 写入响应流
            workbook.write(response.getOutputStream());
            workbook.close();
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("导出服务项目列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateServiceItemStatus(Long serviceId, String status) {
        ServiceItem serviceItem = getById(serviceId);
        if (serviceItem == null) {
            throw new BusinessException("服务项目不存在");
        }
        
        // 验证状态转换是否合法
        validateStatusTransition(serviceItem.getStatus(), status);
        
        serviceItem.setStatus(status);
        return this.updateById(serviceItem);
    }

    /**
     * 将实体转换为VO
     */
    private ServiceItemVO convertToVO(ServiceItem serviceItem) {
        ServiceItemVO vo = new ServiceItemVO();
        BeanUtils.copyProperties(serviceItem, vo);
        // 设置状态名称
        vo.setStatusName("0".equals(serviceItem.getStatus()) ? "正常" : "停用");
        return vo;
    }

    /**
     * 验证状态转换是否合法
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // 定义状态转换规则
        Map<String, Set<String>> allowedTransitions = new HashMap<>();
        allowedTransitions.put("0", Set.of("1")); // 正常 -> 停用
        allowedTransitions.put("1", Set.of("0")); // 停用 -> 正常
        
        Set<String> allowedStatuses = allowedTransitions.get(currentStatus);
        if (allowedStatuses == null || !allowedStatuses.contains(newStatus)) {
            throw new BusinessException("非法的状态转换");
        }
    }
} 