package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.HealthRecords;
import com.communitypension.communitypensionadmin.service.HealthRecordsService;
import com.communitypension.communitypensionadmin.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 健康档案控制器
 * 提供健康档案的查询、添加和更新功能
 */
@RestController
@RequestMapping("/api/health-records/")
@Tag(name = "HealthRecordsController", description = "健康档案管理")
@Slf4j
@Validated
public class HealthRecordsController {
    // 注入健康档案服务
    @Autowired
    private HealthRecordsService healthRecordsService;

    /**
     * 获取健康档案
     * @param elderId 老人ID
     * @return 健康档案信息
     */


   @Operation(summary = "获取健康档案", description = "根据老人ID获取健康档案信息")
    @GetMapping("getHealthRecords") public Result<HealthRecords> getHealthRecords(
            @Parameter(description = "老人ID", required = true)
            @RequestParam("elderId") @NotNull(message = "老人ID不能为空") Long elderId) {
        log.info("获取健康档案，老人ID: {}", elderId);
        try {
            HealthRecords healthRecords = healthRecordsService.getHealthRecordsWithElderInfoByElderId(elderId);
            if (healthRecords == null) {
                log.warn("健康档案不存在，老人ID: {}", elderId);
                return Result.error(404, "健康档案不存在");
            } else {
                return Result.success("查询成功", healthRecords);
            }
        } catch (Exception e) {
            log.error("获取健康档案失败，老人ID: {}, 错误: {}", elderId, e.getMessage(), e);
            // 根据异常类型返回不同的错误信息
            if (e instanceof SecurityException) {
                return Result.error(401, "无效的令牌");
            } else {
                return Result.error(500, "获取健康档案失败: " + e.getMessage());
            }
        }
    }

    /**
     * 添加健康档案
     * @param healthRecords 健康档案信息
     * @return 添加结果
     */
    @PostMapping("/addHealthRecords")
    @Operation(summary = "添加健康档案", description = "添加新的健康档案信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<HealthRecords> addHealthRecords(@RequestBody @Valid HealthRecords healthRecords) {
        log.info("添加健康档案，老人ID: {}", healthRecords.getElderId());
        try {
            // 参数验证
            if (healthRecords.getElderId() == null) {
                return Result.error(400, "老人ID不能为空");
            }

            // 业务验证
            validateHealthData(healthRecords);

            // 检查是否已存在健康档案
            HealthRecords existingRecord = healthRecordsService.getHealthRecordsWithElderInfoByElderId(healthRecords.getElderId());
            if (existingRecord != null) {
                log.warn("健康档案已存在，老人ID: {}", healthRecords.getElderId());
                return Result.error(409, "健康档案已存在，请使用更新接口");
            }

            boolean success = healthRecordsService.save(healthRecords);
            if (success) {
                log.info("健康档案添加成功，老人ID: {}", healthRecords.getElderId());
                return Result.success("添加成功", healthRecords);
            } else {
                log.warn("健康档案添加失败，老人ID: {}", healthRecords.getElderId());
                return Result.error(500, "添加失败");
            }
        } catch (IllegalArgumentException e) {
            log.warn("健康档案添加失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("添加健康档案失败，老人ID: {}, 错误: {}",
                    healthRecords.getElderId(), e.getMessage(), e);
            return Result.error(500, "添加健康档案失败: " + e.getMessage());
        }
    }

    /**
     * 更新健康档案
     * @param healthRecords 健康档案信息
     * @return 更新结果
     */
    @PutMapping("/updateHealthRecords")
    @Operation(summary = "更新健康档案", description = "更新健康档案信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<HealthRecords> updateHealthRecords(@RequestBody @Valid HealthRecords healthRecords) {
        log.info("更新健康档案，ID: {}, 老人ID: {}",
                healthRecords.getId(), healthRecords.getElderId());
        try {
            // 参数验证
            if (healthRecords.getId() == null) {
                return Result.error(400, "记录ID不能为空");
            }
            if (healthRecords.getElderId() == null) {
                return Result.error(400, "老人ID不能为空");
            }

            // 业务验证
            validateHealthData(healthRecords);

            // 检查记录是否存在
            HealthRecords existingRecord = healthRecordsService.getById(healthRecords.getId());
            if (existingRecord == null) {
                log.warn("健康档案不存在，ID: {}", healthRecords.getId());
                return Result.error(404, "健康档案不存在");
            }

            boolean success = healthRecordsService.updateHealthRecordByElderId(healthRecords, healthRecords.getElderId());
            if (success) {
                log.info("健康档案更新成功，ID: {}, 老人ID: {}", healthRecords.getId(), healthRecords.getElderId());
                return Result.success("更新成功", healthRecords);
            } else {
                log.warn("健康档案更新失败，ID: {}, 老人ID: {}", healthRecords.getId(), healthRecords.getElderId());
                return Result.error(500, "更新失败");
            }
        } catch (IllegalArgumentException e) {
            log.warn("健康档案更新失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新健康档案失败，ID: {}, 错误: {}",
                    healthRecords.getId(), e.getMessage(), e);
            return Result.error(500, "更新健康档案失败: " + e.getMessage());
        }
    }

    /**
     * 验证健康数据是否在合理范围内
     * @param healthRecords 健康记录
     * @throws IllegalArgumentException 如果数据不在合理范围内
     */
    private void validateHealthData(HealthRecords healthRecords) throws IllegalArgumentException {
        // 身高验证 (100-250cm)
        if (healthRecords.getHeight() != null) {
            BigDecimal minHeight = new BigDecimal("100");
            BigDecimal maxHeight = new BigDecimal("250");
            if (healthRecords.getHeight().compareTo(minHeight) < 0 ||
                    healthRecords.getHeight().compareTo(maxHeight) > 0) {
                throw new IllegalArgumentException("身高应在100-250cm之间");
            }
        }

        // 体重验证 (30-200kg)
        if (healthRecords.getWeight() != null) {
            BigDecimal minWeight = new BigDecimal("30");
            BigDecimal maxWeight = new BigDecimal("200");
            if (healthRecords.getWeight().compareTo(minWeight) < 0 ||
                    healthRecords.getWeight().compareTo(maxWeight) > 0) {
                throw new IllegalArgumentException("体重应在30-200kg之间");
            }
        }

        // 血压验证 (格式检查)
        if (healthRecords.getBloodPressure() != null) {
            String bp = healthRecords.getBloodPressure();
            if (!bp.matches("^\\d{2,3}/\\d{2,3}$")) {
                throw new IllegalArgumentException("血压格式不正确，应为收缩压/舒张压，如120/80");
            }

            String[] parts = bp.split("/");
            int systolic = Integer.parseInt(parts[0]);
            int diastolic = Integer.parseInt(parts[1]);

            if (systolic < 60 || systolic > 200 || diastolic < 40 || diastolic > 120) {
                throw new IllegalArgumentException("血压数值超出正常范围");
            }
        }

        // 血糖验证 (2-30 mmol/L)
        if (healthRecords.getBloodSugar() != null) {
            BigDecimal minBloodSugar = new BigDecimal("2");
            BigDecimal maxBloodSugar = new BigDecimal("30");
            if (healthRecords.getBloodSugar().compareTo(minBloodSugar) < 0 ||
                    healthRecords.getBloodSugar().compareTo(maxBloodSugar) > 0) {
                throw new IllegalArgumentException("血糖值应在2-30mmol/L之间");
            }
        }
    }

    /**
     * 删除健康档案（可选功能）
     * @param id 健康档案ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除健康档案", description = "删除健康档案信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteHealthRecord(
            @Parameter(description = "健康档案ID", required = true)
            @PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("删除健康档案，ID: {}", id);
        try {
            // 检查记录是否存在
            HealthRecords existingRecord = healthRecordsService.getById(id);
            if (existingRecord == null) {
                log.warn("健康档案不存在，ID: {}", id);
                return Result.error(404, "健康档案不存在");
            }

            boolean success = healthRecordsService.removeById(id);
            if (success) {
                log.info("健康档案删除成功，ID: {}", id);
                return Result.success("删除成功", null);
            } else {
                log.warn("健康档案删除失败，ID: {}", id);
                return Result.error(500, "删除失败");
            }
        } catch (Exception e) {
            log.error("删除健康档案失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Result.error(500, "删除健康档案失败: " + e.getMessage());
        }
    }
}