package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.pojo.dto.HealthAlertDTO;
import com.communitypension.communitypensionadmin.service.HealthAlertService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.pojo.vo.HealthAlertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康预警控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health/alert")
@RequiredArgsConstructor
@Tag(name = "健康预警管理", description = "健康预警相关接口")
@Validated
public class HealthAlertController {

    private final HealthAlertService healthAlertService;

    /**
     * 添加健康预警
     */
    @PostMapping
    @Operation(summary = "添加健康预警", description = "添加健康预警信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> addHealthAlert(@Valid @RequestBody HealthAlertDTO alertDTO) {
        log.info("添加健康预警，老人ID: {}", alertDTO.getElderId());
        try {
            Long alertId = healthAlertService.addHealthAlert(alertDTO);
            log.info("健康预警添加成功，ID: {}", alertId);
            return Result.success("添加成功", alertId);
        } catch (IllegalArgumentException e) {
            log.warn("健康预警添加失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("健康预警添加失败，错误: {}", e.getMessage(), e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新健康预警
     */
    @PutMapping
    @Operation(summary = "更新健康预警", description = "更新健康预警信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateHealthAlert(@Valid @RequestBody HealthAlertDTO alertDTO) {
        log.info("更新健康预警，ID: {}", alertDTO.getId());
        try {
            // 参数验证
            if (alertDTO.getId() == null) {
                return Result.error(400, "预警ID不能为空");
            }

            boolean result = healthAlertService.updateHealthAlert(alertDTO);
            if (result) {
                log.info("健康预警更新成功，ID: {}", alertDTO.getId());
                return Result.success("更新成功");
            } else {
                log.warn("健康预警更新失败，ID: {}", alertDTO.getId());
                return Result.error("更新失败");
            }
        } catch (IllegalArgumentException e) {
            log.warn("健康预警更新失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("健康预警更新失败，错误: {}", e.getMessage(), e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除健康预警
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除健康预警", description = "删除健康预警信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteHealthAlert(
            @Parameter(description = "健康预警ID", required = true)
            @PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("删除健康预警，ID: {}", id);
        try {
            boolean result = healthAlertService.deleteHealthAlert(id);
            if (result) {
                log.info("健康预警删除成功，ID: {}", id);
                return Result.success("删除成功");
            } else {
                log.warn("健康预警删除失败，ID: {}", id);
                return Result.error("删除失败");
            }
        } catch (IllegalArgumentException e) {
            log.warn("健康预警删除失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("健康预警删除失败，错误: {}", e.getMessage(), e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 处理健康预警
     */
    @PutMapping("/handle/{id}")
    @Operation(summary = "处理健康预警", description = "处理健康预警信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> handleHealthAlert(
            @Parameter(description = "健康预警ID", required = true)
            @PathVariable @NotNull(message = "ID不能为空") Long id,
            @Parameter(description = "处理人ID", required = true)
            @RequestParam @NotNull(message = "处理人ID不能为空") Long handlerId,
            @Parameter(description = "处理备注")
            @RequestParam(required = false) String handleNote,
            @Parameter(description = "处理状态：1-已处理，2-已忽略", required = true)
            @RequestParam @NotNull(message = "处理状态不能为空") Integer status) {
        log.info("处理健康预警，ID: {}, 处理人ID: {}, 状态: {}", id, handlerId, status);
        try {
            boolean result = healthAlertService.handleHealthAlert(id, handlerId, handleNote, status);
            if (result) {
                log.info("健康预警处理成功，ID: {}", id);
                return Result.success("处理成功");
            } else {
                log.warn("健康预警处理失败，ID: {}", id);
                return Result.error("处理失败");
            }
        } catch (IllegalArgumentException e) {
            log.warn("健康预警处理失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("健康预警处理失败，错误: {}", e.getMessage(), e);
            return Result.error("处理失败: " + e.getMessage());
        }
    }

    /**
     * 获取健康预警详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取健康预警详情", description = "获取健康预警详情")
    public Result<HealthAlertVO> getHealthAlertDetail(
            @Parameter(description = "健康预警ID", required = true)
            @PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("获取健康预警详情，ID: {}", id);
        try {
            HealthAlertVO alert = healthAlertService.getHealthAlertDetail(id);
            if (alert == null) {
                log.warn("健康预警不存在，ID: {}", id);
                return Result.error(404, "健康预警不存在");
            }
            return Result.success("查询成功", alert);
        } catch (Exception e) {
            log.error("获取健康预警详情失败，错误: {}", e.getMessage(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询健康预警列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询健康预警列表", description = "分页查询健康预警列表")
    public Result<Page<HealthAlertVO>> getHealthAlertList(
            @Parameter(description = "老人ID") @RequestParam(required = false) Long elderId,
            @Parameter(description = "老人姓名") @RequestParam(required = false) String elderName,
            @Parameter(description = "预警类型") @RequestParam(required = false) Integer alertType,
            @Parameter(description = "预警级别") @RequestParam(required = false) Integer alertLevel,
            @Parameter(description = "处理状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页查询健康预警列表，页码: {}, 每页记录数: {}", pageNum, pageSize);
        try {
            // 参数验证
            if (pageNum <= 0) {
                return Result.error(400, "页码必须大于0");
            }
            if (pageSize <= 0 || pageSize > 100) {
                return Result.error(400, "每页记录数必须在1-100之间");
            }

            Page<HealthAlertVO> page = healthAlertService.getHealthAlertList(
                    elderId, elderName, alertType, alertLevel, status, startTime, endTime, pageNum, pageSize);
            return Result.success("查询成功", page);
        } catch (Exception e) {
            log.error("分页查询健康预警列表失败，错误: {}", e.getMessage(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取老人未处理的健康预警数量
     */
    @GetMapping("/count/unhandled/{elderId}")
    @Operation(summary = "获取老人未处理的健康预警数量", description = "获取老人未处理的健康预警数量")
    public Result<Integer> getUnhandledAlertsCount(
            @Parameter(description = "老人ID", required = true)
            @PathVariable @NotNull(message = "老人ID不能为空") Long elderId) {
        log.info("获取老人未处理的健康预警数量，老人ID: {}", elderId);
        try {
            int count = healthAlertService.getUnhandledAlertsCount(elderId);
            return Result.success("查询成功", count);
        } catch (Exception e) {
            log.error("获取老人未处理的健康预警数量失败，错误: {}", e.getMessage(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取老人最近的健康预警列表
     */
    @GetMapping("/recent/{elderId}")
    @Operation(summary = "获取老人最近的健康预警列表", description = "获取老人最近的健康预警列表")
    public Result<List<HealthAlertVO>> getRecentAlerts(
            @Parameter(description = "老人ID", required = true)
            @PathVariable @NotNull(message = "老人ID不能为空") Long elderId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "5") Integer limit) {
        log.info("获取老人最近的健康预警列表，老人ID: {}, 限制数量: {}", elderId, limit);
        try {
            List<HealthAlertVO> alerts = healthAlertService.getRecentAlerts(elderId, limit);
            return Result.success("查询成功", alerts);
        } catch (Exception e) {
            log.error("获取老人最近的健康预警列表失败，错误: {}", e.getMessage(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有未处理的紧急预警
     */
    @GetMapping("/emergency/unhandled")
    @Operation(summary = "获取所有未处理的紧急预警", description = "获取所有未处理的紧急预警")
    public Result<List<HealthAlertVO>> getUnhandledEmergencyAlerts() {
        log.info("获取所有未处理的紧急预警");
        try {
            List<HealthAlertVO> alerts = healthAlertService.getUnhandledEmergencyAlerts();
            return Result.success("查询成功", alerts);
        } catch (Exception e) {
            log.error("获取所有未处理的紧急预警失败，错误: {}", e.getMessage(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 基于健康监测数据生成预警
     */
    @PostMapping("/generate/{monitorId}")
    @Operation(summary = "基于健康监测数据生成预警", description = "基于健康监测数据生成预警")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> generateAlertFromMonitor(
            @Parameter(description = "健康监测ID", required = true)
            @PathVariable @NotNull(message = "健康监测ID不能为空") Long monitorId) {
        log.info("基于健康监测数据生成预警，监测ID: {}", monitorId);
        try {
            Long alertId = healthAlertService.generateAlertFromMonitor(monitorId);
            if (alertId != null) {
                log.info("生成预警成功，预警ID: {}", alertId);
                return Result.success("生成预警成功", alertId);
            } else {
                log.info("未生成预警，监测数据正常");
                return Result.success("未生成预警，监测数据正常");
            }
        } catch (IllegalArgumentException e) {
            log.warn("生成预警失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("生成预警失败，错误: {}", e.getMessage(), e);
            return Result.error("生成预警失败: " + e.getMessage());
        }
    }

    /**
     * 批量添加健康预警
     */
    @PostMapping("/batch")
    @Operation(summary = "批量添加健康预警", description = "批量添加健康预警")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchAddHealthAlerts(@Valid @RequestBody List<HealthAlertDTO> alertDTOs) {
        log.info("批量添加健康预警，数量: {}", alertDTOs.size());
        try {
            boolean result = healthAlertService.batchAddHealthAlerts(alertDTOs);
            if (result) {
                log.info("批量添加健康预警成功");
                return Result.success("添加成功");
            } else {
                log.warn("批量添加健康预警失败");
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            log.error("批量添加健康预警失败，错误: {}", e.getMessage(), e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 发送健康预警通知
     */
    @PostMapping("/notify/{id}")
    @Operation(summary = "发送健康预警通知", description = "发送健康预警通知")
    public Result<Boolean> sendAlertNotification(
            @Parameter(description = "健康预警ID", required = true)
            @PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("发送健康预警通知，ID: {}", id);
        try {
            boolean result = healthAlertService.sendAlertNotification(id);
            if (result) {
                log.info("健康预警通知发送成功，ID: {}", id);
                return Result.success("发送成功", true);
            } else {
                log.warn("健康预警通知发送失败，ID: {}", id);
                return Result.success("发送失败，可能是用户不在线", false);
            }
        } catch (IllegalArgumentException e) {
            log.warn("健康预警通知发送失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("健康预警通知发送失败，错误: {}", e.getMessage(), e);
            return Result.error("发送失败: " + e.getMessage());
        }
    }
} 