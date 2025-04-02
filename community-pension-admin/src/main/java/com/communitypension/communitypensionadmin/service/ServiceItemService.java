package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.dto.ServiceItemDTO;
import com.communitypension.communitypensionadmin.entity.ServiceItem;
import com.communitypension.communitypensionadmin.vo.ServiceItemVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

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

    /**
     * 批量删除服务项目
     *
     * @param serviceIds 服务项目ID列表
     * @return 是否成功
     */
    boolean batchDeleteServiceItems(List<Long> serviceIds);

    /**
     * 导出服务项目列表
     *
     * @param response HTTP响应对象
     * @param serviceItem 查询条件
     */
    void exportServiceItems(HttpServletResponse response, ServiceItem serviceItem);

    /**
     * 更新服务项目状态
     *
     * @param serviceId 服务项目ID
     * @param status 状态：0-正常 1-停用
     * @return 是否成功
     */
    boolean updateServiceItemStatus(Long serviceId, String status);
} 