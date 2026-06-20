package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.utils.FileUploadUtil;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.pojo.dto.ActivityDTO;
import com.communitypension.communitypensionadmin.pojo.dto.ActivityQuery;
import com.communitypension.communitypensionadmin.pojo.dto.StatusUpdateDTO;
import com.communitypension.communitypensionadmin.service.ActivityService;
import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.pojo.vo.ActivityVO;
import com.communitypension.communitypensionadmin.pojo.vo.DictDataVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileUploadUtil fileUploadUtil;

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
     * @param id 活动ID
     * @param dto 活动数据
     * @return 结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateActivity(
            @PathVariable Long id, 
            @Validated @RequestBody ActivityDTO dto) {
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
    
    /**
     * 上传活动图片
     * 
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.fail("只能上传图片文件");
            }
            
            // 检查文件大小
            long fileSize = file.getSize();
            long maxFileSize = 5 * 1024 * 1024; // 5MB
            
            if (fileSize > maxFileSize) {
                return Result.fail("文件大小超过限制，最大允许5MB");
            }
            
            // 上传文件到activities文件夹
            String fileUrl = fileUploadUtil.uploadFile(file, "activities");
            return Result.success("上传成功", fileUrl);
        } catch (Exception e) {
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/types")
    public Result<List<DictDataVO>> getActivityTypes() {
        return Result.success(dictDataService.getDictDataByType("activity_type"));
    }


}