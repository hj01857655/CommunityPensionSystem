package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.pojo.dto.ServiceOrderDTO;
import com.communitypension.communitypensionadmin.entity.ServiceOrder;
import com.communitypension.communitypensionadmin.pojo.vo.ServiceOrderVO;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 服务预约Service接口
 */
public interface ServiceOrderService extends IService<ServiceOrder> {
    
    /**
     * 分页查询预约列表
     *
     * @param page 分页参数
     * @param order 查询条件
     * @return 预约列表
     */
    Page<ServiceOrderVO> getOrderList(Page<ServiceOrder> page, ServiceOrder order);
    
    /**
     * 根据ID获取预约详情
     *
     * @param orderId 预约ID
     * @return 预约详情
     */
    ServiceOrderVO getOrderById(Long orderId);
    
    /**
     * 创建预约
     *
     * @param orderDTO 预约信息
     * @return 是否成功
     */
    boolean createOrder(ServiceOrderDTO orderDTO);
    
    /**
     * 修改预约
     *
     * @param orderDTO 预约信息
     * @return 是否成功
     */
    boolean updateOrder(ServiceOrderDTO orderDTO);
    
    /**
     * 取消预约
     *
     * @param orderId 预约ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId, String reason);
    
    /**
     * 审核预约
     *
     * @param orderId 预约ID
     * @param status 审核状态(1-通过 2-拒绝)
     * @param remark 审核备注
     * @return 是否成功
     */
    boolean reviewOrder(Long orderId, Integer status, String remark);
    
    /**
     * 开始服务
     *
     * @param orderId 预约ID
     * @return 是否成功
     */
    boolean startService(Long orderId);
    
    /**
     * 完成服务
     *
     * @param orderId 预约ID
     * @param duration 实际时长（分钟）
     * @param fee 实际费用
     * @return 是否成功
     */
    boolean completeService(Long orderId, Integer duration, Double fee);
    
    /**
     * 检查时间冲突
     *
     * @param serviceItemId 服务项目ID
     * @param scheduleTime 预约时间
     * @param duration 服务时长（分钟）
     * @return 是否存在冲突
     */
    boolean checkTimeConflict(Long serviceItemId, LocalDateTime scheduleTime, Integer duration);
    
    /**
     * 导出预约列表
     *
     * @param response HTTP响应对象
     * @param order 查询条件
     */
    void exportOrders(HttpServletResponse response, ServiceOrder order);
    
    /**
     * 获取用户预约列表
     *
     * @param userId 用户ID
     * @param status 预约状态
     * @return 预约列表
     */
    List<ServiceOrderVO> getUserOrders(Long userId, Integer status);
    
    /**
     * 获取服务项目预约统计
     *
     * @param serviceItemId 服务项目ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    Map<String, Object> getOrderStats(Long serviceItemId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 删除预约
     *
     * @param orderIds 预约ID字符串，多个ID用逗号分隔
     * @return 是否成功
     */
    boolean deleteOrder(String orderIds);
}