package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.dto.ServiceItemDTO;
import com.communitypension.communitypensionadmin.entity.ServiceItem;
import com.communitypension.communitypensionadmin.mapper.ServiceItemMapper;
import com.communitypension.communitypensionadmin.service.ServiceItemService;
import com.communitypension.communitypensionadmin.vo.ServiceItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 服务项目Service实现类
 */
@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {

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

    /**
     * 将实体转换为VO
     */
    private ServiceItemVO convertToVO(ServiceItem serviceItem) {
        ServiceItemVO vo = new ServiceItemVO();
        BeanUtils.copyProperties(serviceItem, vo);
        return vo;
    }
} 