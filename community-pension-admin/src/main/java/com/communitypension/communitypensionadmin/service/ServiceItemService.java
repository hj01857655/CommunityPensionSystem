package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.dto.ServiceItemDTO;
import com.communitypension.communitypensionadmin.entity.ServiceItem;
import com.communitypension.communitypensionadmin.vo.ServiceItemVO;

/**
 * 服务项目Service接口
 */
public interface ServiceItemService extends IService<ServiceItem> {
    
    /**
     * 分页查询服务项目列表
     *
     * @param page 分页参数
     * @param serviceItem 查询条件
     * @return 服务项目列表
     */
    Page<ServiceItemVO> getServiceItemList(Page<ServiceItem> page, ServiceItem serviceItem);

    /**
     * 根据ID获取服务项目详情
     *
     * @param serviceId 服务项目ID
     * @return 服务项目详情
     */
    ServiceItemVO getServiceItemById(Long serviceId);

    /**
     * 新增服务项目
     *
     * @param serviceItemDTO 服务项目信息
     * @return 是否成功
     */
    boolean addServiceItem(ServiceItemDTO serviceItemDTO);

    /**
     * 修改服务项目
     *
     * @param serviceItemDTO 服务项目信息
     * @return 是否成功
     */
    boolean updateServiceItem(ServiceItemDTO serviceItemDTO);

    /**
     * 删除服务项目
     *
     * @param serviceId 服务项目ID
     * @return 是否成功
     */
    boolean deleteServiceItem(Long serviceId);
} 