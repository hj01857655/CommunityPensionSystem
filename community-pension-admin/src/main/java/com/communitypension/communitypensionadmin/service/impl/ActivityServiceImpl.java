package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.constant.DictTypeConstants;
import com.communitypension.communitypensionadmin.dto.ActivityDTO;
import com.communitypension.communitypensionadmin.dto.ActivityQuery;
import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.ActivityMapper;
import com.communitypension.communitypensionadmin.service.ActivityService;
import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.utils.DictUtils;
import com.communitypension.communitypensionadmin.vo.ActivityVO;
import com.communitypension.communitypensionadmin.vo.DictDataVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 活动Service实现类
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    
    private final DictDataService dictDataService;
    
    @Override
    public Page<ActivityVO> getActivityList(ActivityQuery query) {
        Page<Activity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        wrapper.like(StringUtils.hasText(query.getTitle()), Activity::getTitle, query.getTitle())
               .eq(query.getStatus() != null, Activity::getStatus, query.getStatus())
               .eq(StringUtils.hasText(query.getType()), Activity::getType, query.getType())
               .ge(query.getStartTime() != null, Activity::getStartTime, query.getStartTime())
               .le(query.getEndTime() != null, Activity::getEndTime, query.getEndTime())
               .orderByDesc(Activity::getCreatedAt);
        
        // 执行分页查询
        Page<Activity> activityPage = page(page, wrapper);
        
        // 转换为VO
        List<ActivityVO> voList = activityPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Page<ActivityVO> resultPage = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        resultPage.setRecords(voList);
        
        return resultPage;
    }
    
    private ActivityVO convertToVO(Activity activity) {
        ActivityVO vo = new ActivityVO();
        BeanUtils.copyProperties(activity, vo);
        
        // 设置活动类型名称
        if (StringUtils.hasText(activity.getType())) {
            DictDataVO dictDatavVO =  dictDataService.getDictDataByTypeAndValue("activity_type", activity.getType());
            if (dictDatavVO != null) {
                vo.setTypeName(dictDatavVO.getDictLabel());
            } else {
                vo.setTypeName("");
            }
        } else {
            vo.setTypeName("未设置类型");
        }
        
        // 设置状态名称
        vo.setStatusName(getStatusName(activity.getStatus()));
        
        return vo;
    }
    
    private String getStatusName(Integer status) {
        return switch (status) {
            case 0 -> "筹备中";
            case 1 -> "报名中";
            case 2 -> "进行中";
            case 3 -> "已结束";
            case 4 -> "已取消";
            default -> "未知状态";
        };
    }
    /**
     * 获取活动详情
     */
    @Override
    public ActivityVO getActivityDetail(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        return convertToVO(activity);
    }
    /**
     * 创建活动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(ActivityDTO dto) {
        // 验证活动时间
        validateActivityTime(dto.getStartTime(), dto.getEndTime());
        
        Activity activity = new Activity();
        BeanUtils.copyProperties(dto, activity);
        activity.setStatus(0); // 默认为筹备中状态
        
        save(activity);
    }
    /**
     * 更新活动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(Long id, ActivityDTO dto) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        
        // 验证活动时间
        validateActivityTime(dto.getStartTime(), dto.getEndTime());
        
        BeanUtils.copyProperties(dto, activity);
        updateById(activity);
    }
    /**
     * 验证活动时间
     */
    private void validateActivityTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
    }
    /**
     * 更新活动状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityStatus(Long id, Integer status) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        
        // 验证状态转换是否合法
        validateStatusTransition(activity.getStatus(), status);
        
        activity.setStatus(status);
        updateById(activity);
    }

    /**
     *  验证状态转换是否合法
     * @param currentStatus 当前状态
     * @param newStatus 新状态
     */
    private void validateStatusTransition(Integer currentStatus, Integer newStatus) {
        // 定义状态转换规则
        Map<Integer, Set<Integer>> allowedTransitions = new HashMap<>();
        allowedTransitions.put(0, Set.of(1, 4)); // 筹备中 -> 报名中/已取消
        allowedTransitions.put(1, Set.of(2, 4)); // 报名中 -> 进行中/已取消
        allowedTransitions.put(2, Set.of(3, 4)); // 进行中 -> 已结束/已取消
        
        Set<Integer> allowedStatuses = allowedTransitions.get(currentStatus);
        if (allowedStatuses == null || !allowedStatuses.contains(newStatus)) {
            throw new BusinessException("非法的状态转换");
        }
    }

    /**
     * 删除活动
     * @param id 活动ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        
        // 只有已结束或已取消的活动可以删除
        if (activity.getStatus() != 3 && activity.getStatus() != 4) {
            throw new BusinessException("只能删除已结束或已取消的活动");
        }
        
        removeById(id);
    }
    
    /**
     * 获取活动统计信息
     */
    @Override
    public Map<String, Object> getActivityStats(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalParticipants", baseMapper.getCurrentParticipants(id));
        stats.put("maxParticipants", activity.getMaxParticipants());
        stats.put("remainingSlots", activity.getMaxParticipants() - baseMapper.getCurrentParticipants(id));
        
        return stats;
    }
    
    /**
     * 导出活动列表
     */
    @Override
    public void exportActivityList(String title, Integer status,
                                 String startTime, String endTime, HttpServletResponse response) {
        // 构建查询条件
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<Activity>()
            .like(StringUtils.hasText(title), Activity::getTitle, title)
            .eq(status != null, Activity::getStatus, status)
            .ge(StringUtils.hasText(startTime), Activity::getStartTime, parseDateTime(startTime))
            .le(StringUtils.hasText(endTime), Activity::getEndTime, parseDateTime(endTime))
            .orderByDesc(Activity::getCreatedAt);
        
        // 查询数据
        List<Activity> activities = baseMapper.selectList(wrapper);
        
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("活动列表");
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"活动ID", "活动标题", "活动类型", "活动内容", "开始时间", "结束时间", "状态", "参与人数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // 填充数据
        int rowNum = 1;
        for (Activity activity : activities) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(activity.getId());
            row.createCell(1).setCellValue(activity.getTitle());
            // 添加活动类型
            if (activity.getType() != null) {
                row.createCell(2).setCellValue(DictUtils.getDictLabel(DictTypeConstants.ACTIVITY_TYPE, String.valueOf(activity.getType())));
            }
            row.createCell(4).setCellValue(activity.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            row.createCell(5).setCellValue(activity.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            row.createCell(6).setCellValue(getStatusName(activity.getStatus()));
            row.createCell(7).setCellValue(baseMapper.getCurrentParticipants(activity.getId()));
        }
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=activities.xlsx");
        
        // 写入响应流
        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }
    
    /**
     * 解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (!StringUtils.hasText(dateTimeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new RuntimeException("日期时间格式错误，应为：yyyy-MM-dd HH:mm:ss", e);
        }
    }
} 