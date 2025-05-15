package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.dto.HealthMonitorDTO;
import com.communitypension.communitypensionadmin.service.HealthAlertService;
import com.communitypension.communitypensionadmin.service.HealthMonitorService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.vo.HealthMonitorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 健康监测Controller
 */
@RestController
@RequestMapping("/api/health/monitor")
@RequiredArgsConstructor
@Tag(name = "健康监测管理", description = "健康监测相关接口")
@Slf4j
public class HealthMonitorController {

    private final HealthMonitorService healthMonitorService;
    private final HealthAlertService healthAlertService;

    /**
     * 添加健康监测记录
     */
    @PostMapping
    @Operation(summary = "添加健康监测记录", description = "添加健康监测记录")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> addHealthMonitor(@Valid @RequestBody HealthMonitorDTO monitorDTO) {
        Long monitorId = healthMonitorService.addHealthMonitor(monitorDTO);
        return Result.success("添加成功", monitorId);
    }

    /**
     * 更新健康监测记录
     */
    @PutMapping
    @Operation(summary = "更新健康监测记录", description = "更新健康监测记录")
    public Result<Void> updateHealthMonitor(@Valid @RequestBody HealthMonitorDTO monitorDTO) {
        boolean result = healthMonitorService.updateHealthMonitor(monitorDTO);
        return result ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除健康监测记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除健康监测记录", description = "删除健康监测记录")
    public Result<Void> deleteHealthMonitor(@PathVariable Long id) {
        boolean result = healthMonitorService.deleteHealthMonitor(id);
        return result ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取健康监测详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取健康监测详情", description = "获取健康监测详情")
    public Result<HealthMonitorVO> getHealthMonitorDetail(@PathVariable Long id) {
        return Result.success(healthMonitorService.getHealthMonitorDetail(id));
    }

    /**
     * 分页查询健康监测列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询健康监测列表", description = "分页查询健康监测列表")
    public Result<Page<HealthMonitorVO>> getHealthMonitorList(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String elderName,
            @RequestParam(required = false) Integer monitorType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(healthMonitorService.getHealthMonitorList(elderId, elderName, monitorType, startDate, endDate, pageNum, pageSize));
    }

    /**
     * 获取老人的最新健康监测记录
     */
    @GetMapping("/latest/{elderId}")
    @Operation(summary = "获取老人的最新健康监测记录", description = "获取老人的最新健康监测记录")
    public Result<List<HealthMonitorVO>> getElderLatestMonitors(@PathVariable Long elderId) {
        return Result.success(healthMonitorService.getElderLatestMonitors(elderId));
    }

    /**
     * 获取老人的健康监测统计
     */
    @GetMapping("/stats/{elderId}")
    @Operation(summary = "获取老人的健康监测统计", description = "获取老人的健康监测统计")
    public Result<Map<String, Object>> getElderMonitorStats(
            @PathVariable Long elderId,
            @RequestParam(required = false) Integer monitorType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(healthMonitorService.getElderMonitorStats(elderId, monitorType, startDate, endDate));
    }

    /**
     * 批量添加健康监测记录
     */
    @PostMapping("/batch")
    @Operation(summary = "批量添加健康监测记录", description = "批量添加健康监测记录")
    public Result<Void> batchAddHealthMonitors(@Valid @RequestBody List<HealthMonitorDTO> monitorDTOs) {
        boolean result = healthMonitorService.batchAddHealthMonitors(monitorDTOs);
        return result ? Result.success("批量添加成功") : Result.error("批量添加失败");
    }

    /**
     * 手动从监测记录生成健康预警
     */
    @PostMapping("/generate-alert/{id}")
    @Operation(summary = "手动从监测记录生成健康预警", description = "根据健康监测记录手动生成健康预警")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> generateAlertFromMonitor(
            @Parameter(description = "监测记录ID", required = true)
            @PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("手动从监测记录生成健康预警，监测ID: {}", id);
        try {
            Long alertId = healthAlertService.generateAlertFromMonitor(id);
            if (alertId != null) {
                log.info("生成健康预警成功，预警ID: {}", alertId);
                return Result.success("生成健康预警成功", alertId);
            } else {
                log.info("未生成健康预警，监测数据正常");
                return Result.success("未生成健康预警，监测数据正常");
            }
        } catch (IllegalArgumentException e) {
            log.warn("生成健康预警失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("生成健康预警失败，错误: {}", e.getMessage(), e);
            return Result.error("生成健康预警失败: " + e.getMessage());
        }
    }
}
