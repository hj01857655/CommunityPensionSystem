package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.dto.HealthRecordDTO;
import com.communitypension.communitypensionadmin.service.HealthRecordService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.vo.HealthRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 健康档案Controller
 */
@RestController
@RequestMapping("/api/health/record")
@RequiredArgsConstructor
@Tag(name = "健康档案管理", description = "健康档案相关接口")
@Slf4j
@Validated
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    /**
     * 添加健康档案
     */
    @PostMapping
    @Operation(summary = "添加健康档案", description = "添加健康档案")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> addHealthRecord(@Valid @RequestBody HealthRecordDTO recordDTO) {
        log.info("添加健康档案，老人ID: {}", recordDTO.getElderId());
        try {
            // 验证健康数据
            validateHealthData(recordDTO);

            Long recordId = healthRecordService.addHealthRecord(recordDTO);
            log.info("健康档案添加成功，ID: {}", recordId);
            return Result.success("添加成功", recordId);
        } catch (IllegalArgumentException e) {
            log.warn("健康档案添加失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("添加健康档案失败，老人ID: {}, 错误: {}", recordDTO.getElderId(), e.getMessage(), e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新健康档案
     */
    @PutMapping
    @Operation(summary = "更新健康档案", description = "更新健康档案")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateHealthRecord(@Valid @RequestBody HealthRecordDTO recordDTO) {
        log.info("更新健康档案，ID: {}, 老人ID: {}", recordDTO.getId(), recordDTO.getElderId());
        try {
            // 参数验证
            if (recordDTO.getId() == null) {
                return Result.error(400, "记录ID不能为空");
            }
            if (recordDTO.getElderId() == null) {
                return Result.error(400, "老人ID不能为空");
            }

            // 验证健康数据
            validateHealthData(recordDTO);

            boolean result = healthRecordService.updateHealthRecord(recordDTO);
            if (result) {
                log.info("健康档案更新成功，ID: {}", recordDTO.getId());
                return Result.success("更新成功");
            } else {
                log.warn("健康档案更新失败，ID: {}", recordDTO.getId());
                return Result.error("更新失败");
            }
        } catch (IllegalArgumentException e) {
            log.warn("健康档案更新失败，参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新健康档案失败，ID: {}, 错误: {}", recordDTO.getId(), e.getMessage(), e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除健康档案
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除健康档案", description = "删除健康档案")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteHealthRecord(
            @Parameter(description = "健康档案ID", required = true)
            @PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("删除健康档案，ID: {}", id);
        try {
            // 检查记录是否存在
            HealthRecordVO existingRecord = healthRecordService.getHealthRecordDetail(id);
            if (existingRecord == null) {
                log.warn("健康档案不存在，ID: {}", id);
                return Result.error(404, "健康档案不存在");
            }

            boolean result = healthRecordService.deleteHealthRecord(id);
            if (result) {
                log.info("健康档案删除成功，ID: {}", id);
                return Result.success("删除成功");
            } else {
                log.warn("健康档案删除失败，ID: {}", id);
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除健康档案失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取健康档案详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取健康档案详情", description = "获取健康档案详情")
    public Result<HealthRecordVO> getHealthRecordDetail(
            @Parameter(description = "健康档案ID", required = true)
            @PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("获取健康档案详情，ID: {}", id);
        try {
            HealthRecordVO record = healthRecordService.getHealthRecordDetail(id);
            if (record == null) {
                log.warn("健康档案不存在，ID: {}", id);
                return Result.error(404, "健康档案不存在");
            }
            return Result.success("查询成功", record);
        } catch (Exception e) {
            log.error("获取健康档案详情失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取老人的健康档案
     */
    @GetMapping("/elder/{elderId}")
    @Operation(summary = "获取老人的健康档案", description = "获取老人的健康档案")
    public Result<HealthRecordVO> getElderHealthRecord(
            @Parameter(description = "老人ID", required = true)
            @PathVariable @NotNull(message = "老人ID不能为空") Long elderId) {
        log.info("获取老人的健康档案，老人ID: {}", elderId);
        try {
            HealthRecordVO record = healthRecordService.getElderHealthRecord(elderId);
            if (record == null) {
                log.warn("健康档案不存在，老人ID: {}", elderId);
                return Result.error(404, "健康档案不存在");
            }
            return Result.success("查询成功", record);
        } catch (Exception e) {
            log.error("获取老人健康档案失败，老人ID: {}, 错误: {}", elderId, e.getMessage(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询健康档案列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询健康档案列表", description = "分页查询健康档案列表")
    public Result<Page<HealthRecordVO>> getHealthRecordList(
            @Parameter(description = "老人姓名") @RequestParam(required = false) String elderName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页查询健康档案列表，页码: {}, 每页记录数: {}, 老人姓名: {}", pageNum, pageSize, elderName);
        try {
            // 参数验证
            if (pageNum <= 0) {
                return Result.error(400, "页码必须大于0");
            }
            if (pageSize <= 0 || pageSize > 100) {
                return Result.error(400, "每页记录数必须在1-100之间");
            }

            Page<HealthRecordVO> page = healthRecordService.getHealthRecordList(elderName, pageNum, pageSize);
            return Result.success("查询成功", page);
        } catch (Exception e) {
            log.error("分页查询健康档案列表失败，错误: {}", e.getMessage(), e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 兼容旧接口：获取健康档案
     */
    @GetMapping("/getHealthRecords")
    @Operation(summary = "获取健康档案(兼容旧接口)", description = "根据老人ID获取健康档案信息")
    public Result<HealthRecordVO> getHealthRecords(
            @Parameter(description = "老人ID", required = true)
            @RequestParam("elderId") @NotNull(message = "老人ID不能为空") Long elderId) {
        log.info("兼容旧接口：获取健康档案，老人ID: {}", elderId);
        return getElderHealthRecord(elderId);
    }

    /**
     * 兼容旧接口：添加健康档案
     */
    @PostMapping("/addHealthRecords")
    @Operation(summary = "添加健康档案(兼容旧接口)", description = "添加新的健康档案信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> addHealthRecords(@RequestBody @Valid HealthRecordDTO recordDTO) {
        log.info("兼容旧接口：添加健康档案，老人ID: {}", recordDTO.getElderId());
        return addHealthRecord(recordDTO);
    }

    /**
     * 兼容旧接口：更新健康档案
     */
    @PutMapping("/updateHealthRecords")
    @Operation(summary = "更新健康档案(兼容旧接口)", description = "更新健康档案信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateHealthRecords(@RequestBody @Valid HealthRecordDTO recordDTO) {
        log.info("兼容旧接口：更新健康档案，ID: {}, 老人ID: {}", recordDTO.getId(), recordDTO.getElderId());
        return updateHealthRecord(recordDTO);
    }

    /**
     * 导出健康档案
     */
    @GetMapping("/export")
    @Operation(summary = "导出健康档案", description = "导出健康档案数据")
    public void exportHealthRecords(HttpServletResponse response) {
        log.info("导出健康档案");
        try {
            // 假设有一个服务方法可以导出数据
            byte[] data = healthRecordService.exportHealthRecords();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=health_records.xlsx");
            response.getOutputStream().write(data);
            response.flushBuffer();
        } catch (Exception e) {
            log.error("导出健康档案失败，错误: {}", e.getMessage(), e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 批量添加健康档案
     */
    @PostMapping("/batch")
    @Operation(summary = "批量添加健康档案", description = "批量添加健康档案信息")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchAddHealthRecords(@RequestBody List<HealthRecordDTO> recordDTOs) {
        log.info("批量添加健康档案");
        try {
            for (HealthRecordDTO recordDTO : recordDTOs) {
                healthRecordService.addHealthRecord(recordDTO);
            }
            return Result.success("批量添加成功");
        } catch (Exception e) {
            log.error("批量添加健康档案失败，错误: {}", e.getMessage(), e);
            return Result.error("批量添加失败: " + e.getMessage());
        }
    }

    /**
     * 验证健康数据是否在合理范围内
     *
     * @param recordDTO 健康记录DTO
     * @throws IllegalArgumentException 如果数据不在合理范围内
     */
    private void validateHealthData(HealthRecordDTO recordDTO) throws IllegalArgumentException {
        // 身高验证 (100-250cm)
        if (recordDTO.getHeight() != null) {
            BigDecimal minHeight = new BigDecimal("100");
            BigDecimal maxHeight = new BigDecimal("250");
            if (recordDTO.getHeight().compareTo(minHeight) < 0 || recordDTO.getHeight().compareTo(maxHeight) > 0) {
                throw new IllegalArgumentException("身高应在100-250cm之间");
            }
        }

        // 体重验证 (30-200kg)
        if (recordDTO.getWeight() != null) {
            BigDecimal minWeight = new BigDecimal("30");
            BigDecimal maxWeight = new BigDecimal("200");
            if (recordDTO.getWeight().compareTo(minWeight) < 0 || recordDTO.getWeight().compareTo(maxWeight) > 0) {
                throw new IllegalArgumentException("体重应在30-200kg之间");
            }
        }

        // 血压验证 (格式检查)
        if (recordDTO.getBloodPressure() != null && !recordDTO.getBloodPressure().isEmpty()) {
            String bp = recordDTO.getBloodPressure();
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
        if (recordDTO.getBloodSugar() != null) {
            BigDecimal minBloodSugar = new BigDecimal("2");
            BigDecimal maxBloodSugar = new BigDecimal("30");
            if (recordDTO.getBloodSugar().compareTo(minBloodSugar) < 0 || recordDTO.getBloodSugar().compareTo(maxBloodSugar) > 0) {
                throw new IllegalArgumentException("血糖值应在2-30mmol/L之间");
            }
        }

        // 体温验证 (35-42℃)
        if (recordDTO.getTemperature() != null) {
            BigDecimal minTemp = new BigDecimal("35");
            BigDecimal maxTemp = new BigDecimal("42");
            if (recordDTO.getTemperature().compareTo(minTemp) < 0 || recordDTO.getTemperature().compareTo(maxTemp) > 0) {
                throw new IllegalArgumentException("体温应在35-42℃之间");
            }
        }

        // 心率验证 (40-200 bpm)
        if (recordDTO.getHeartRate() != null) {
            if (recordDTO.getHeartRate() < 40 || recordDTO.getHeartRate() > 200) {
                throw new IllegalArgumentException("心率应在40-200 bpm之间");
            }
        }
    }
}
