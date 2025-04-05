package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.dto.HealthRecordDTO;
import com.communitypension.communitypensionadmin.service.HealthRecordService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.vo.HealthRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 健康档案Controller
 */
@RestController
@RequestMapping("/api/health/record")
@RequiredArgsConstructor
@Tag(name = "健康档案管理", description = "健康档案相关接口")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    /**
     * 添加健康档案
     */
    @PostMapping
    @Operation(summary = "添加健康档案", description = "添加健康档案")
    public Result<Long> addHealthRecord(@Valid @RequestBody HealthRecordDTO recordDTO) {
        Long recordId = healthRecordService.addHealthRecord(recordDTO);
        return Result.success("添加成功", recordId);
    }

    /**
     * 更新健康档案
     */
    @PutMapping
    @Operation(summary = "更新健康档案", description = "更新健康档案")
    public Result<Void> updateHealthRecord(@Valid @RequestBody HealthRecordDTO recordDTO) {
        boolean result = healthRecordService.updateHealthRecord(recordDTO);
        return result ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除健康档案
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除健康档案", description = "删除健康档案")
    public Result<Void> deleteHealthRecord(@PathVariable Long id) {
        boolean result = healthRecordService.deleteHealthRecord(id);
        return result ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取健康档案详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取健康档案详情", description = "获取健康档案详情")
    public Result<HealthRecordVO> getHealthRecordDetail(@PathVariable Long id) {
        return Result.success(healthRecordService.getHealthRecordDetail(id));
    }

    /**
     * 获取老人的健康档案
     */
    @GetMapping("/elder/{elderId}")
    @Operation(summary = "获取老人的健康档案", description = "获取老人的健康档案")
    public Result<HealthRecordVO> getElderHealthRecord(@PathVariable Long elderId) {
        return Result.success(healthRecordService.getElderHealthRecord(elderId));
    }

    /**
     * 分页查询健康档案列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询健康档案列表", description = "分页查询健康档案列表")
    public Result<Page<HealthRecordVO>> getHealthRecordList(
            @RequestParam(required = false) String elderName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(healthRecordService.getHealthRecordList(elderName, pageNum, pageSize));
    }
}
