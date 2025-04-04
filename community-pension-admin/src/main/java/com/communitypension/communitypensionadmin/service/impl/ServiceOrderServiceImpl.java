package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.constant.DictTypeConstants;
import com.communitypension.communitypensionadmin.dto.ServiceOrderDTO;
import com.communitypension.communitypensionadmin.entity.ServiceItem;
import com.communitypension.communitypensionadmin.entity.ServiceOrder;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.enums.ServiceOrderStatus;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.ServiceOrderMapper;
import com.communitypension.communitypensionadmin.service.ServiceItemService;
import com.communitypension.communitypensionadmin.service.ServiceOrderService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.service.NotificationService;
import com.communitypension.communitypensionadmin.utils.DictUtils;
import com.communitypension.communitypensionadmin.vo.ServiceOrderVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务预约Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {
    
    private final ServiceItemService serviceItemService;
    private final UserService userService;
    private final DictDataService dictDataService;
    private final NotificationService notificationService;
    
    @Override
    public Page<ServiceOrderVO> getOrderList(Page<ServiceOrder> page, ServiceOrder order) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (order != null) {
            if (order.getUserId() != null) {
                wrapper.eq(ServiceOrder::getUserId, order.getUserId());
            }
            if (order.getServiceItemId() != null) {
                wrapper.eq(ServiceOrder::getServiceItemId, order.getServiceItemId());
            }
            if (order.getStatus() != null) {
                wrapper.eq(ServiceOrder::getStatus, order.getStatus());
            }
            if (StringUtils.hasText(order.getApplyReason())) {
                wrapper.like(ServiceOrder::getApplyReason, order.getApplyReason());
            }
            if (order.getScheduleTime() != null) {
                wrapper.eq(ServiceOrder::getScheduleTime, order.getScheduleTime());
            }
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(ServiceOrder::getCreateTime);
        
        // 执行分页查询
        Page<ServiceOrder> orderPage = this.page(page, wrapper);
        
        // 转换为VO
        List<ServiceOrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Page<ServiceOrderVO> resultPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        resultPage.setRecords(voList);
        
        return resultPage;
    }
    
    @Override
    public ServiceOrderVO getOrderById(Long orderId) {
        ServiceOrder order = this.getById(orderId);
        return order != null ? convertToVO(order) : null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(ServiceOrderDTO orderDTO) {
        try {
            // 检查服务项目是否存在
            ServiceItem serviceItem = serviceItemService.getById(orderDTO.getServiceItemId());
            if (serviceItem == null) {
                throw new BusinessException("服务项目不存在");
            }


            // 检查时间冲突
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建预约失败: {}", e.getMessage(), e);
            throw new BusinessException("创建预约失败：" + e.getMessage());
        }
        return false;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(ServiceOrderDTO orderDTO) {
        try {
            ServiceOrder order = this.getById(orderDTO.getId());
            if (order == null) {
                throw new BusinessException("预约记录不存在");
            }
            
            // 只有待审核状态的预约可以修改
            if (order.getStatus() != ServiceOrderStatus.PENDING) {
                throw new BusinessException("当前状态不允许修改");
            }
            
            // 检查服务项目是否存在
            ServiceItem serviceItem = serviceItemService.getById(orderDTO.getServiceItemId());
            if (serviceItem == null) {
                throw new BusinessException("服务项目不存在");
            }
            

            
            // 检查时间冲突
            if (checkTimeConflict(orderDTO.getServiceItemId(), orderDTO.getScheduleTime(), serviceItem.getDuration())) {
                throw new BusinessException("该时间段已被预约");
            }
            
            // 检查预约时间是否在服务时间内
            if (!isWithinServiceTime(orderDTO.getScheduleTime(), serviceItem.getDuration())) {
                throw new BusinessException("预约时间不在服务时间内");
            }
            
            // 更新预约信息
            BeanUtils.copyProperties(orderDTO, order);
            
            // 保存更新
            boolean success = this.updateById(order);
            
            if (success) {
                // 发送修改通知
                User user = userService.getById(order.getUserId());
                if (user != null) {
                    notificationService.sendOrderNotification(order, user, "预约信息已更新");
                }
            }
            
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改预约失败: {}", e.getMessage(), e);
            throw new BusinessException("修改预约失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, String reason) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        // 只有待审核或已派单状态的预约可以取消
        if (order.getStatus() != ServiceOrderStatus.PENDING && order.getStatus() !=ServiceOrderStatus.ASSIGNED) {
            throw new BusinessException("当前状态不允许取消");
        }
        
        // 更新状态为已取消
        order.setStatus(ServiceOrderStatus.CANCELLED);
        order.setReviewRemark(reason);
        
        // 保存更新
        boolean success = this.updateById(order);
        
        if (success) {
            // 发送取消通知
            sendOrderNotification(order, "预约已取消");
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewOrder(Long orderId, Integer status, String remark) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        // 只有待审核状态的预约可以审核
        if (order.getStatus() != ServiceOrderStatus.PENDING) {
            throw new BusinessException("当前状态不允许审核");
        }
        
        // 更新状态
        order.setStatus(status == 1 ?ServiceOrderStatus.ASSIGNED : ServiceOrderStatus.REJECTED); // 1-通过 2-拒绝
        order.setReviewRemark(remark);
        
        // 保存更新
        boolean success = this.updateById(order);
        
        if (success) {
            // 发送审核结果通知
            sendOrderNotification(order, status == 1 ? "预约审核通过" : "预约审核拒绝");
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startService(Long orderId) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        // 只有已派单状态的预约可以开始服务
        if (order.getStatus() != ServiceOrderStatus.ASSIGNED) {
            throw new BusinessException("当前状态不允许开始服务");
        }
        
        // 更新状态为服务中
        order.setStatus(ServiceOrderStatus.IN_PROGRESS);
        
        // 保存更新
        boolean success = this.updateById(order);
        
        if (success) {
            // 发送服务开始通知
            sendOrderNotification(order, "服务已开始");
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeService(Long orderId, Integer duration, Double fee) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        // 只有服务中状态的预约可以完成
        if (order.getStatus() != ServiceOrderStatus.IN_PROGRESS) {
            throw new BusinessException("当前状态不允许完成服务");
        }
        
        // 更新实际时长和费用
        order.setActualDuration(duration);
        order.setActualFee(fee);
        order.setStatus(ServiceOrderStatus.COMPLETED); // 更新为已完成状态
        
        // 保存更新
        boolean success = this.updateById(order);
        
        if (success) {
            // 发送完成通知
            User user = userService.getById(order.getUserId());
            if (user != null) {
                notificationService.sendOrderNotification(order, user, "服务已完成");
            }
        }
        
        return success;
    }
    
    @Override
    public boolean checkTimeConflict(Long serviceItemId, LocalDateTime scheduleTime, Integer duration) {
        // 计算服务结束时间
        LocalDateTime endTime = scheduleTime.plusMinutes(duration);
        
        // 查询该时间段内的所有预约
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getServiceItemId, serviceItemId)
               .ne(ServiceOrder::getStatus, ServiceOrderStatus.CANCELLED) // 排除已取消的预约
               .and(w -> w
                   .between(ServiceOrder::getScheduleTime, scheduleTime, endTime)
                   .or()
                   .between(ServiceOrder::getScheduleTime, scheduleTime.minusMinutes(duration), scheduleTime)
               );
        
        return this.count(wrapper) > 0;
    }
    
    @Override
    public void exportOrders(HttpServletResponse response, ServiceOrder order) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
            if (order != null) {
                if (order.getUserId() != null) {
                    wrapper.eq(ServiceOrder::getUserId, order.getUserId());
                }
                if (order.getServiceItemId() != null) {
                    wrapper.eq(ServiceOrder::getServiceItemId, order.getServiceItemId());
                }
                if (order.getStatus() != null) {
                    wrapper.eq(ServiceOrder::getStatus, order.getStatus());
                }
            }
            
            // 查询数据
            List<ServiceOrder> orders = this.list(wrapper);
            
            // 获取用户信息
            Set<Long> userIds = orders.stream()
                    .map(ServiceOrder::getUserId)
                    .collect(Collectors.toSet());
            Map<Long, User> userMap = userService.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getUserId, user -> user));
            
            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("服务预约记录");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"预约ID", "用户姓名", "服务项目", "预约时间", "状态", "实际时长", "实际费用"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据
            int rowNum = 1;
            for (ServiceOrder orderItem : orders) {
                Row row = sheet.createRow(rowNum++);
                User user = userMap.get(orderItem.getUserId());
                
                row.createCell(0).setCellValue(orderItem.getId());
                row.createCell(1).setCellValue(user != null ? user.getName() : "");
                row.createCell(2).setCellValue(orderItem.getServiceName());
                row.createCell(3).setCellValue(orderItem.getScheduleTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                row.createCell(4).setCellValue(DictUtils.getDictLabel("service_order_status", orderItem.getStatus().toString()));
                row.createCell(5).setCellValue(orderItem.getActualDuration() != null ? orderItem.getActualDuration() + "分钟" : "");
                row.createCell(6).setCellValue(orderItem.getActualFee() != null ? orderItem.getActualFee() + "元" : "");
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("服务预约记录.xlsx", StandardCharsets.UTF_8));
            
            // 写入响应流
            workbook.write(response.getOutputStream());
            workbook.close();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("导出服务预约记录失败: {}", e.getMessage(), e);
            throw new BusinessException("导出服务预约记录失败：" + e.getMessage());
        }
    }
    
    @Override
    public List<ServiceOrderVO> getUserOrders(Long userId, Integer status) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getUserId, userId);
        if (status != null) {
            wrapper.eq(ServiceOrder::getStatus, status);
        }
        wrapper.orderByDesc(ServiceOrder::getCreateTime);
        
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getOrderStats(Long serviceItemId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getServiceItemId, serviceItemId)
               .between(ServiceOrder::getScheduleTime, startTime, endTime)
               .eq(ServiceOrder::getStatus, 3); // 只统计已完成的预约
        
        List<ServiceOrder> orders = this.list(wrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orders.size());
        stats.put("totalDuration", orders.stream().mapToInt(ServiceOrder::getActualDuration).sum());
        stats.put("totalFee", orders.stream().mapToDouble(ServiceOrder::getActualFee).sum());
        stats.put("averageDuration", orders.isEmpty() ? 0 : orders.stream().mapToInt(ServiceOrder::getActualDuration).average().orElse(0));
        stats.put("averageFee", orders.isEmpty() ? 0 : orders.stream().mapToDouble(ServiceOrder::getActualFee).average().orElse(0));
        
        return stats;
    }
    
    /**
     * 将实体转换为VO
     */
    private ServiceOrderVO convertToVO(ServiceOrder order) {
        ServiceOrderVO vo = new ServiceOrderVO();
        BeanUtils.copyProperties(order, vo);
        
        // 设置用户信息
        User user = userService.getById(order.getUserId());
        if (user != null) {
            vo.setUserName(user.getName());
        }
        
        // 设置服务项目信息
        ServiceItem serviceItem = serviceItemService.getById(order.getServiceItemId());
        if (serviceItem != null) {
            vo.setServiceName(serviceItem.getServiceName());
            vo.setServiceType(serviceItem.getServiceType());
            vo.setServiceTypeName(DictUtils.getDictLabel(DictTypeConstants.SERVICE_TYPE, serviceItem.getServiceType()));
        }
        
        // 设置状态名称
        vo.setStatusName(DictUtils.getDictLabel(DictTypeConstants.ORDER_STATUS, String.valueOf(order.getStatus())));
        
        vo.setActualDuration(order.getActualDuration());
        vo.setActualFee(order.getActualFee());
        vo.setRemark(order.getRemark());
        
        return vo;
    }
    
    /**
     * 发送预约通知
     */
    private void sendOrderNotification(ServiceOrder order, String message) {
        try {
            User user = userService.getById(order.getUserId());
            if (user != null) {
                notificationService.sendOrderNotification(order, user, message);
            }
        } catch (Exception e) {
            log.error("发送预约通知失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查预约时间是否在服务时间内
     */
    private boolean isWithinServiceTime(LocalDateTime scheduleTime, Integer duration) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        
        // 检查预约时间是否大于当前时间
        if (scheduleTime.isBefore(now)) {
            return false;
        }
        
        // 检查预约时间是否在服务时间内（例如：8:00-18:00）
        int hour = scheduleTime.getHour();
        if (hour < 8 || hour >= 18) {
            return false;
        }
        
        // 检查服务结束时间是否超过服务时间
        LocalDateTime endTime = scheduleTime.plusMinutes(duration);
        return endTime.getHour() < 18;
    }
    
    /**
     * 检查用户是否有未完成的预约
     */
    private boolean hasUnfinishedOrder(Long userId) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getUserId, userId)
               .in(ServiceOrder::getStatus,
                       ServiceOrderStatus.PENDING,
                       ServiceOrderStatus.ASSIGNED,
                       ServiceOrderStatus.IN_PROGRESS); // 待审核、已派单、服务中状态
        
        return this.count(wrapper) > 0;
    }
} 