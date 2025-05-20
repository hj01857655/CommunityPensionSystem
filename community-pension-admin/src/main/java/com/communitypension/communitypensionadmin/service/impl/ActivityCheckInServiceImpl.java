package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.entity.ActivityCheckIn;
import com.communitypension.communitypensionadmin.entity.ActivityRegister;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.ActivityCheckInMapper;
import com.communitypension.communitypensionadmin.service.ActivityCheckInService;
import com.communitypension.communitypensionadmin.service.ActivityRegisterService;
import com.communitypension.communitypensionadmin.service.ActivityService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.pojo.vo.ActivityCheckInVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 活动签到Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityCheckInServiceImpl extends ServiceImpl<ActivityCheckInMapper, ActivityCheckIn> implements ActivityCheckInService {

    private final ActivityService activityService;
    private final ActivityRegisterService activityRegisterService;
    private final UserService userService;

    /**
     * 用户签到
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkIn(Long registerId, Long checkInUserId, Integer isProxyCheckIn) {
        // 获取报名记录
        ActivityRegister register = activityRegisterService.getById(registerId);
        if (register == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 只有已通过状态的报名可以签到
        if (register.getStatus() != 1) {
            throw new BusinessException("只有已通过审核的报名可以签到");
        }

        // 获取活动信息
        Activity activity = activityService.getById(register.getActivityId());
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 检查活动状态是否为“进行中”
        if (activity.getStatus() != 2) {
            throw new BusinessException("只有进行中的活动才能签到");
        }

        // 检查是否已经签到
        ActivityCheckIn existingCheckIn = baseMapper.getCheckInByRegisterId(registerId);
        if (existingCheckIn != null) {
            throw new BusinessException("已经签到，请勿重复操作");
        }

        // 创建签到记录
        ActivityCheckIn checkIn = new ActivityCheckIn();
        checkIn.setRegisterId(registerId);
        checkIn.setActivityId(register.getActivityId());
        checkIn.setElderId(register.getElderId());
        checkIn.setCheckInUserId(checkInUserId);
        checkIn.setIsProxyCheckIn(isProxyCheckIn);
        checkIn.setSignInTime(LocalDateTime.now());

        // 保存签到记录
        boolean result = save(checkIn);

        // 更新报名状态为已签到
        if (result) {
            register.setStatus(4); // 4-已签到
            activityRegisterService.updateById(register);
        }

        return result;
    }

    /**
     * 批量签到
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCheckIn(List<Long> registerIds, Long checkInUserId, Integer isProxyCheckIn) {
        if (registerIds == null || registerIds.isEmpty()) {
            return false;
        }

        // 首先检查所有报名记录对应的活动状态
        Map<Long, ActivityRegister> registerMap = activityRegisterService.listByIds(registerIds).stream()
                .collect(Collectors.toMap(ActivityRegister::getId, register -> register));

        // 获取所有活动ID
        Set<Long> activityIds = registerMap.values().stream()
                .map(ActivityRegister::getActivityId)
                .collect(Collectors.toSet());

        // 检查活动状态
        Map<Long, Activity> activityMap = activityService.listByIds(activityIds).stream()
                .collect(Collectors.toMap(Activity::getId, activity -> activity));

        // 验证所有活动是否都处于“进行中”状态
        for (ActivityRegister register : registerMap.values()) {
            Activity activity = activityMap.get(register.getActivityId());
            if (activity == null || activity.getStatus() != 2) {
                throw new BusinessException("只有进行中的活动才能签到");
            }

            // 检查报名状态
            if (register.getStatus() != 1) {
                throw new BusinessException("只有已通过审核的报名可以签到");
            }
        }

        boolean allSuccess = true;
        for (Long id : registerIds) {
            try {
                if (!checkIn(id, checkInUserId, isProxyCheckIn)) {
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
     * 签退
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signOut(Long registerId) {
        // 获取签到记录
        ActivityCheckIn checkIn = baseMapper.getCheckInByRegisterId(registerId);
        if (checkIn == null) {
            throw new BusinessException("签到记录不存在，无法签退");
        }

        // 更新签退时间
        checkIn.setSignOutTime(LocalDateTime.now());
        return updateById(checkIn);
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
        LambdaQueryWrapper<ActivityRegister> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegister::getActivityId, activityId)
                .eq(ActivityRegister::getStatus, 1); // 1-已通过
        long approvedCount = activityRegisterService.count(wrapper);

        // 查询已签到的记录数
        int checkedInCount = baseMapper.getCheckInCount(activityId);
        long notCheckedInCount = approvedCount - checkedInCount;

        // 计算签到率
        double checkInRate = approvedCount > 0 ? (double) checkedInCount / approvedCount * 100 : 0;

        // 构建统计结果
        Map<String, Object> stats = new HashMap<>();
        stats.put("activityId", activityId);
        stats.put("activityTitle", activity.getTitle());
        stats.put("approvedCount", approvedCount);
        stats.put("checkedInCount", (long) checkedInCount);
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

        // 查询签到记录
        LambdaQueryWrapper<ActivityCheckIn> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.orderByDesc(ActivityCheckIn::getCreatedAt);

        // 使用自定义SQL查询关联报名表
        // 这里需要自定义SQL或使用MyBatis-Plus的连表查询
        // 简化起见，这里先查询所有签到记录，然后在内存中过滤
        Page<ActivityCheckIn> checkInPage = page(page, checkInWrapper);

        // 获取报名ID列表
        List<Long> registerIds = checkInPage.getRecords().stream()
                .map(ActivityCheckIn::getRegisterId)
                .collect(Collectors.toList());

        if (registerIds.isEmpty()) {
            // 没有签到记录，返回空结果
            Page<ActivityCheckInVO> resultPage = new Page<>(pageNum, pageSize, 0);
            resultPage.setRecords(new ArrayList<>());
            return resultPage;
        }

        // 查询报名记录
        Map<Long, ActivityRegister> registerMap = activityRegisterService.listByIds(registerIds).stream()
                .collect(Collectors.toMap(ActivityRegister::getId, register -> register));

        // 过滤出指定活动的签到记录
        List<ActivityCheckIn> filteredCheckIns = checkInPage.getRecords().stream()
                .filter(checkIn -> {
                    ActivityRegister register = registerMap.get(checkIn.getRegisterId());
                    return register != null && register.getActivityId().equals(activityId);
                })
                .collect(Collectors.toList());

        // 获取老人ID和签到人ID列表
        List<Long> userIds = new ArrayList<>();
        for (ActivityCheckIn checkIn : filteredCheckIns) {
            ActivityRegister register = registerMap.get(checkIn.getRegisterId());
            if (register != null) {
                userIds.add(register.getElderId());
                userIds.add(checkIn.getCheckInUserId());
            }
        }

        // 查询用户信息
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        // 查询活动信息
        Activity activity = activityService.getById(activityId);

        // 转换为VO
        List<ActivityCheckInVO> voList = filteredCheckIns.stream()
                .map(checkIn -> {
                    ActivityRegister register = registerMap.get(checkIn.getRegisterId());
                    User elder = register != null ? userMap.get(register.getElderId()) : null;
                    User checkInUser = userMap.get(checkIn.getCheckInUserId());

                    ActivityCheckInVO vo = new ActivityCheckInVO();
                    BeanUtils.copyProperties(checkIn, vo);

                    if (register != null) {
                        vo.setElderId(register.getElderId());
                        vo.setActivityId(register.getActivityId());
                    }

                    if (activity != null) {
                        vo.setActivityTitle(activity.getTitle());
                    }

                    if (elder != null) {
                        vo.setElderName(elder.getUsername());
                    }

                    if (checkInUser != null) {
                        vo.setCheckInUserName(checkInUser.getUsername());
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        // 构建返回结果
        Page<ActivityCheckInVO> resultPage = new Page<>(checkInPage.getCurrent(), checkInPage.getSize(), filteredCheckIns.size());
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
            wrapper.orderByDesc(ActivityCheckIn::getCreatedAt);
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
     * 根据报名ID获取签到记录
     */
    @Override
    public ActivityCheckInVO getCheckInByRegisterId(Long registerId) {
        ActivityCheckIn checkIn = baseMapper.getCheckInByRegisterId(registerId);
        if (checkIn == null) {
            return null;
        }

        ActivityRegister register = activityRegisterService.getById(registerId);
        if (register == null) {
            return null;
        }

        // 查询用户信息
        User elder = userService.getById(register.getElderId());
        User checkInUser = userService.getById(checkIn.getCheckInUserId());

        // 查询活动信息
        Activity activity = activityService.getById(register.getActivityId());

        ActivityCheckInVO vo = new ActivityCheckInVO();
        BeanUtils.copyProperties(checkIn, vo);

        vo.setRegisterId(registerId);
        vo.setActivityId(register.getActivityId());
        vo.setElderId(register.getElderId());

        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }

        if (elder != null) {
            vo.setElderName(elder.getUsername());
        }

        if (checkInUser != null) {
            vo.setCheckInUserName(checkInUser.getUsername());
        }

        return vo;
    }

    /**
     * 检查老人是否已签到
     */
    @Override
    public boolean checkElderCheckedIn(Long activityId, Long elderId) {
        Integer count = baseMapper.checkElderCheckedIn(activityId, elderId);
        return count != null && count > 0;
    }
}
