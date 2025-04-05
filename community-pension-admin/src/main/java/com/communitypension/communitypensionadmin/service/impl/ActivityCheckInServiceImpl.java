package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.entity.ActivityCheckIn;
import com.communitypension.communitypensionadmin.entity.ActivityParticipate;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.ActivityCheckInMapper;
import com.communitypension.communitypensionadmin.service.ActivityCheckInService;
import com.communitypension.communitypensionadmin.service.ActivityParticipateService;
import com.communitypension.communitypensionadmin.service.ActivityService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.vo.ActivityCheckInVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动签到Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityCheckInServiceImpl extends ServiceImpl<ActivityCheckInMapper, ActivityCheckIn> implements ActivityCheckInService {

    private final ActivityService activityService;
    private final ActivityParticipateService activityParticipateService;
    private final UserService userService;

    /**
     * 用户签到
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkIn(Long participateId) {
        // 获取报名记录
        ActivityParticipate participate = activityParticipateService.getById(participateId);
        if (participate == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 只有已通过状态的报名可以签到
        if (participate.getStatus() != 1) {
            throw new BusinessException("只有已通过审核的报名可以签到");
        }

        // 检查是否已经签到
        ActivityCheckIn existingCheckIn = baseMapper.getCheckInByParticipateId(participateId);
        if (existingCheckIn != null && existingCheckIn.getStatus() == 1) {
            throw new BusinessException("已经签到，请勿重复操作");
        }

        // 创建或更新签到记录
        ActivityCheckIn checkIn;
        if (existingCheckIn != null) {
            checkIn = existingCheckIn;
            checkIn.setStatus(1);
            checkIn.setCheckInTime(LocalDateTime.now());
        } else {
            checkIn = new ActivityCheckIn();
            checkIn.setActivityId(participate.getActivityId());
            checkIn.setUserId(participate.getUserId());
            checkIn.setParticipateId(participateId);
            checkIn.setStatus(1);
            checkIn.setCheckInTime(LocalDateTime.now());
        }

        // 保存签到记录
        boolean result = saveOrUpdate(checkIn);

        // 更新报名状态为已签到
        if (result) {
            participate.setStatus(4); // 4-已签到
            activityParticipateService.updateById(participate);
        }

        return result;
    }

    /**
     * 批量签到
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCheckIn(List<Long> participateIds) {
        if (participateIds == null || participateIds.isEmpty()) {
            return false;
        }

        boolean allSuccess = true;
        for (Long id : participateIds) {
            try {
                if (!checkIn(id)) {
                    allSuccess = false;
                }
            } catch (Exception e) {
                // 记录异常但继续处理其他ID
                log.error("签到失败，ID: {}, 原因: {}", id, e.getMessage());
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    /**
     * 获取用户签到状态
     */
    @Override
    public Integer getUserCheckInStatus(Long activityId, Long userId) {
        return baseMapper.getUserCheckInStatus(activityId, userId);
    }

    /**
     * 获取活动签到统计
     */
    @Override
    public Map<String, Object> getCheckInStats(Long activityId) {
        // 检查活动是否存在
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 查询已通过审核的报名记录
        LambdaQueryWrapper<ActivityParticipate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityParticipate::getActivityId, activityId)
               .in(ActivityParticipate::getStatus, 1, 4); // 1-已通过，4-已签到
        int totalCount = activityParticipateService.count(wrapper);

        // 查询已签到的记录数
        int checkedInCount = baseMapper.getCheckInCount(activityId);
        int notCheckedInCount = totalCount - checkedInCount;

        // 计算签到率
        double checkInRate = totalCount > 0 ? (double) checkedInCount / totalCount * 100 : 0;

        // 构建统计结果
        Map<String, Object> stats = new HashMap<>();
        stats.put("activityId", activityId);
        stats.put("activityTitle", activity.getTitle());
        stats.put("totalCount", totalCount);
        stats.put("checkedInCount", checkedInCount);
        stats.put("notCheckedInCount", notCheckedInCount);
        stats.put("checkInRate", String.format("%.2f%%", checkInRate));

        return stats;
    }

    /**
     * 分页查询活动签到记录
     */
    @Override
    public Page<ActivityCheckInVO> getCheckInList(Long activityId, Integer pageNum, Integer pageSize) {
        Page<ActivityCheckIn> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ActivityCheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityCheckIn::getActivityId, activityId)
               .orderByDesc(ActivityCheckIn::getUpdatedAt);

        Page<ActivityCheckIn> checkInPage = page(page, wrapper);

        // 转换为VO
        List<ActivityCheckInVO> voList = checkInPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<ActivityCheckInVO> resultPage = new Page<>(checkInPage.getCurrent(), checkInPage.getSize(), checkInPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    /**
     * 导出活动签到记录
     */
    @Override
    public void exportCheckInList(Long activityId, HttpServletResponse response) {
        try {
            // 检查活动是否存在
            Activity activity = activityService.getById(activityId);
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }

            // 查询活动签到记录
            LambdaQueryWrapper<ActivityCheckIn> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivityCheckIn::getActivityId, activityId)
                   .orderByDesc(ActivityCheckIn::getUpdatedAt);
            List<ActivityCheckIn> checkIns = list(wrapper);

            // 设置响应头
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = "activity_checkin_" + activityId + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);

            // TODO: 使用Excel导出工具（如EasyExcel）导出数据
            // 这里需要添加相关依赖并实现导出逻辑
            // 例如：
            // EasyExcel.write(response.getOutputStream(), ActivityCheckInExportVO.class)
            //          .sheet("活动签到记录")
            //          .doWrite(convertToExportVOList(checkIns));

            // 暂时返回简单的文本信息
            response.getWriter().write("导出功能待实现，需要添加Excel导出相关依赖");
        } catch (IOException e) {
            log.error("导出活动签到记录失败: {}", e.getMessage(), e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 将实体转换为VO
     */
    private ActivityCheckInVO convertToVO(ActivityCheckIn checkIn) {
        ActivityCheckInVO vo = new ActivityCheckInVO();
        BeanUtils.copyProperties(checkIn, vo);

        // 设置活动标题
        Activity activity = activityService.getById(checkIn.getActivityId());
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }

        // 设置用户姓名
        User user = userService.getById(checkIn.getUserId());
        if (user != null) {
            vo.setUserName(user.getUsername());
        }

        // 设置状态名称
        vo.setStatusName(getStatusName(checkIn.getStatus()));

        return vo;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知状态";
        }

        return switch (status) {
            case 0 -> "未签到";
            case 1 -> "已签到";
            default -> "未知状态";
        };
    }
}
