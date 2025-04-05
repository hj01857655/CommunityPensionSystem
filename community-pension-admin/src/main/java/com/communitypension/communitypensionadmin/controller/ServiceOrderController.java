package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.dto.ServiceOrderDTO;
import com.communitypension.communitypensionadmin.entity.ServiceOrder;
import com.communitypension.communitypensionadmin.service.ServiceOrderService;
import com.communitypension.communitypensionadmin.vo.ServiceOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 服务预约Controller
 */
@Tag(name = "服务预约管理")
@RestController
@RequestMapping("/api/service/order")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @Operation(summary = "分页查询预约列表")
    @GetMapping("/list")
    public Result<Page<ServiceOrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            ServiceOrder order) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        return Result.success(serviceOrderService.getOrderList(page, order));
    }

    /**
     * 获取预约详情
     * @param orderId 预约ID
     * @return Result<ServiceOrderVO>
     */
    @Operation(summary = "获取预约详情")
    @GetMapping("/{orderId}")
    public Result<ServiceOrderVO> getOrderById(@PathVariable Long orderId) {
        return Result.success(serviceOrderService.getOrderById(orderId));
    }

    @Operation(summary = "创建预约")
    @PostMapping
    public Result<Boolean> createOrder(@Valid @RequestBody ServiceOrderDTO orderDTO) {
        return Result.success(serviceOrderService.createOrder(orderDTO));
    }

    @Operation(summary = "修改预约")
    @PutMapping
    public Result<Boolean> updateOrder(@Valid @RequestBody ServiceOrderDTO orderDTO) {
        return Result.success(serviceOrderService.updateOrder(orderDTO));
    }

    @Operation(summary = "取消预约")
    @PostMapping("/{orderId}/cancel")
    public Result<Boolean> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) {
        return Result.success(serviceOrderService.cancelOrder(orderId, reason));
    }

    @Operation(summary = "审核预约")
    @PostMapping("/{orderId}/review")
    public Result<Boolean> reviewOrder(
            @PathVariable Long orderId,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        return Result.success(serviceOrderService.reviewOrder(orderId, status, remark));
    }

    @Operation(summary = "开始服务")
    @PostMapping("/{orderId}/start")
    public Result<Boolean> startService(@PathVariable Long orderId) {
        return Result.success(serviceOrderService.startService(orderId));
    }

    @Operation(summary = "完成服务")
    @PostMapping("/{orderId}/complete")
    public Result<Boolean> completeService(
            @PathVariable Long orderId,
            @RequestParam Integer duration,
            @RequestParam Double fee) {
        return Result.success(serviceOrderService.completeService(orderId, duration, fee));
    }

    @Operation(summary = "检查时间冲突")
    @GetMapping("/check-conflict")
    public Result<Boolean> checkTimeConflict(
            @RequestParam Long serviceItemId,
            @RequestParam LocalDateTime scheduleTime,
            @RequestParam Integer duration) {
        return Result.success(serviceOrderService.checkTimeConflict(serviceItemId, scheduleTime, duration));
    }

    @Operation(summary = "导出预约列表")
    @GetMapping("/export")
    public void exportOrders(HttpServletResponse response, ServiceOrder order) {
        serviceOrderService.exportOrders(response, order);
    }

    @Operation(summary = "获取用户预约列表")
    @GetMapping("/user/{userId}")
    public Result<List<ServiceOrderVO>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer status) {
        return Result.success(serviceOrderService.getUserOrders(userId, status));
    }

    @Operation(summary = "获取服务项目预约统计")
    @GetMapping("/stats/{serviceItemId}")
    public Result<Map<String, Object>> getOrderStats(
            @PathVariable Long serviceItemId,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        return Result.success(serviceOrderService.getOrderStats(serviceItemId, startTime, endTime));
    }
}