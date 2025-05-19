package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.dto.ActivityDTO;
import com.communitypension.communitypensionadmin.dto.ActivityQuery;
import com.communitypension.communitypensionadmin.dto.StatusUpdateDTO;
import com.communitypension.communitypensionadmin.service.ActivityService;
import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.vo.ActivityVO;
import com.communitypension.communitypensionadmin.vo.DictDataVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 活动管理Controller
 */
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final DictDataService dictDataService;

    /**
     * 分页查询活动列表
     */
    @GetMapping("/list")
    public Result<Page<ActivityVO>> getActivityList(ActivityQuery query) {
        return Result.success(activityService.getActivityList(query));
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public Result<ActivityVO> getActivityDetail(@PathVariable Long id) {
        return Result.success(activityService.getActivityDetail(id));
    }

    /**
     * 创建活动
     */
    @PostMapping
    public Result<Void> createActivity(@Validated @RequestBody ActivityDTO dto) {
        activityService.createActivity(dto);
        return Result.success("创建成功");
    }

    /**
     * 更新活动
     */
    @PutMapping("/{id}")
    public Result<Void> updateActivity(@PathVariable Long id, @Validated @RequestBody ActivityDTO dto) {
        activityService.updateActivity(id, dto);
        return Result.success("更新成功");
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return Result.success("删除成功");
    }

    /**
     * 更新活动状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateActivityStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO statusDto) {
        activityService.updateActivityStatus(id, statusDto.getStatus());
        return Result.success("状态更新成功");
    }

    /**
     * 获取活动统计
     */
    @GetMapping("/{id}/stats")
    public Result<Map<String, Object>> getStats(@PathVariable Long id) {
        Map<String, Object> stats = activityService.getActivityStats(id);
        return Result.success(stats);
    }

    /**
     * 导出活动列表
     */
    @GetMapping("/export")
    public void exportList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) {
        activityService.exportActivityList(title, status, startTime, endTime, response);
    }

    @GetMapping("/types")
    public Result<List<DictDataVO>> getActivityTypes() {
        return Result.success(dictDataService.getDictDataByType("activity_type"));
    }


}