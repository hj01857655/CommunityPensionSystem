package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.dto.ServiceReviewAuditDTO;
import com.communitypension.communitypensionadmin.dto.ServiceReviewDTO;
import com.communitypension.communitypensionadmin.dto.ServiceReviewReplyDTO;
import com.communitypension.communitypensionadmin.service.ServiceReviewService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.vo.ServiceReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 服务评价Controller
 */
@RestController
@RequestMapping("/api/service/review")
@RequiredArgsConstructor
@Tag(name = "服务评价管理", description = "服务评价相关接口")
public class ServiceReviewController {
    private final ServiceReviewService serviceReviewService;

    /**
     * 添加服务评价
     */
    @PostMapping
    @Operation(summary = "添加服务评价", description = "添加服务评价")
    public Result<Long> addServiceReview(@Valid @RequestBody ServiceReviewDTO reviewDTO) {
        Long reviewId = serviceReviewService.addServiceReview(reviewDTO);
        return Result.success("添加评价成功", reviewId);
    }

    /**
     * 更新服务评价
     */
    @PutMapping
    @Operation(summary = "更新服务评价", description = "更新服务评价")
    public Result<Void> updateServiceReview(@Valid @RequestBody ServiceReviewDTO reviewDTO) {
        boolean result = serviceReviewService.updateServiceReview(reviewDTO);
        return result ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 获取服务评价详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取服务评价详情", description = "获取服务评价详情")
    public Result<ServiceReviewVO> getServiceReviewDetail(@PathVariable Long id) {
        return Result.success(serviceReviewService.getServiceReviewDetail(id));
    }

    /**
     * 分页查询服务评价列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询服务评价列表", description = "分页查询服务评价列表")
    public Result<Page<ServiceReviewVO>> getServiceReviewList(
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(serviceReviewService.getServiceReviewList(serviceId, status, pageNum, pageSize));
    }

    /**
     * 获取老人的评价列表
     */
    @GetMapping("/elder/{elderId}")
    @Operation(summary = "获取老人的评价列表", description = "获取老人的评价列表")
    public Result<List<ServiceReviewVO>> getElderReviewList(@PathVariable Long elderId) {
        return Result.success(serviceReviewService.getElderReviewList(elderId));
    }

    /**
     * 获取服务评价统计
     */
    @GetMapping("/stats/{serviceId}")
    @Operation(summary = "获取服务评价统计", description = "获取服务评价统计")
    public Result<Map<String, Object>> getServiceReviewStats(@PathVariable Long serviceId) {
        return Result.success(serviceReviewService.getServiceReviewStats(serviceId));
    }

    /**
     * 检查用户是否已评价服务预约
     */
    @GetMapping("/check")
    @Operation(summary = "检查用户是否已评价服务预约", description = "检查用户是否已评价服务预约")
    public Result<Boolean> checkReviewExists(
            @RequestParam Long orderId,
            @RequestParam Long elderId) {
        return Result.success(serviceReviewService.checkReviewExists(orderId, elderId));
    }

    /**
     * 回复服务评价
     */
    @PostMapping("/reply")
    @Operation(summary = "回复服务评价", description = "回复服务评价")
    public Result<Void> replyServiceReview(@Valid @RequestBody ServiceReviewReplyDTO replyDTO) {
        boolean result = serviceReviewService.replyServiceReview(replyDTO);
        return result ? Result.success("回复成功") : Result.error("回复失败");
    }

    /**
     * 审核服务评价
     */
    @PostMapping("/audit")
    @Operation(summary = "审核服务评价", description = "审核服务评价")
    public Result<Void> auditServiceReview(@Valid @RequestBody ServiceReviewAuditDTO auditDTO) {
        boolean result = serviceReviewService.auditServiceReview(auditDTO);
        return result ? Result.success("审核成功") : Result.error("审核失败");
    }

    /**
     * 删除服务评价
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除服务评价", description = "删除服务评价")
    public Result<Void> deleteServiceReview(@PathVariable Long id) {
        boolean result = serviceReviewService.deleteServiceReview(id);
        return result ? Result.success("删除成功") : Result.error("删除失败");
    }
}
