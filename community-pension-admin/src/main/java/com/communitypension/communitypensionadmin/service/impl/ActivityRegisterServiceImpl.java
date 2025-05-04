package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.entity.ActivityRegister;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.ActivityRegisterMapper;
import com.communitypension.communitypensionadmin.service.ActivityRegisterService;
import com.communitypension.communitypensionadmin.service.ActivityService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.vo.ActivityRegisterVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动报名Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityRegisterServiceImpl extends ServiceImpl<ActivityRegisterMapper, ActivityRegister> implements ActivityRegisterService {

    private final ActivityService activityService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerActivity(Long activityId, Long elderId, Long registerUserId, Integer registerType) {
        // 检查活动是否存在
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 检查活动状态是否为报名中
        if (activity.getStatus() != 1) {
            throw new BusinessException("活动不在报名阶段");
        }

        // 检查老人是否已报名
        Integer status = getElderActivityStatus(activityId, elderId);
        if (status != null) {
            // 如果已报名，返回当前报名状态
            String statusName = switch (status) {
                case 0 -> "待审核";
                case 1 -> "已通过";
                case 2 -> "已拒绝";
                case 3 -> "已取消";
                case 4 -> "已签到";
                default -> "未知状态";
            };
            throw new BusinessException("您已报名此活动，当前状态：" + statusName);
        }

        // 检查报名人数是否已满
        int currentRegisters = baseMapper.getCurrentRegisters(activityId);
        if (activity.getMaxParticipants() != null && currentRegisters >= activity.getMaxParticipants()) {
            throw new BusinessException("活动报名人数已满");
        }

        // 创建报名记录
        ActivityRegister register = new ActivityRegister();
        register.setActivityId(activityId);
        register.setElderId(elderId);
        register.setRegisterUserId(registerUserId);
        register.setRegisterType(registerType);
        register.setStatus(0); // 默认待审核
        register.setRegisterTime(LocalDateTime.now());

        return save(register);
    }

    @Override
    public Integer getElderActivityStatus(Long activityId, Long elderId) {
        return baseMapper.getElderActivityStatus(activityId, elderId);
    }

    @Override
    public Long getRegisterIdByActivityAndElder(Long activityId, Long elderId) {
        return baseMapper.getRegisterIdByActivityAndElder(activityId, elderId);
    }

    @Override
    public Page<ActivityRegisterVO> getActivityRegisterList(Long activityId, Integer pageNum, Integer pageSize) {
        Page<ActivityRegister> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ActivityRegister> wrapper = new LambdaQueryWrapper<>();
        if (activityId != null) {
            wrapper.eq(ActivityRegister::getActivityId, activityId);
        }
        wrapper.orderByDesc(ActivityRegister::getCreatedAt);

        Page<ActivityRegister> registerPage = page(page, wrapper);

        // 转换为VO
        List<ActivityRegisterVO> voList = registerPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<ActivityRegisterVO> resultPage = new Page<>(registerPage.getCurrent(), registerPage.getSize(), registerPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    public Page<ActivityRegisterVO> getElderRegisterList(Long elderId, Integer pageNum, Integer pageSize) {
        Page<ActivityRegister> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ActivityRegister> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegister::getElderId, elderId)
               .orderByDesc(ActivityRegister::getCreatedAt);

        Page<ActivityRegister> registerPage = page(page, wrapper);

        // 转换为VO
        List<ActivityRegisterVO> voList = registerPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<ActivityRegisterVO> resultPage = new Page<>(registerPage.getCurrent(), registerPage.getSize(), registerPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRegisterStatus(Long id, Integer status) {
        ActivityRegister register = getById(id);
        if (register == null) {
            throw new BusinessException("报名记录不存在");
        }

        register.setStatus(status);
        return updateById(register);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelRegister(Long id, Long elderId) {
        ActivityRegister register = getById(id);
        if (register == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 验证是否是当前老人的报名记录
        if (!register.getElderId().equals(elderId)) {
            throw new BusinessException("无权操作此报名记录");
        }

        // 更新状态为已取消
        register.setStatus(3);
        return updateById(register);
    }

    /**
     * 审核报名
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditRegister(Long id, Integer status, String remark) {
        ActivityRegister register = getById(id);
        if (register == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 只有待审核状态的报名可以审核
        if (register.getStatus() != 0) {
            throw new BusinessException("只能审核待审核状态的报名");
        }

        register.setStatus(status);
        register.setRemark(remark);
        return updateById(register);
    }

    /**
     * 批量审核报名
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAuditRegister(List<Long> ids, Integer status, String remark) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        for (Long id : ids) {
            try {
                auditRegister(id, status, remark);
            } catch (Exception e) {
                // 记录异常但继续处理其他ID
                log.error("审核报名失败，ID: {}, 原因: {}", id, e.getMessage());
            }
        }

        return true;
    }

    /**
     * 检查老人是否已签到
     */
    @Override
    public boolean checkElderCheckedIn(Long activityId, Long elderId) {
        Integer count = baseMapper.checkElderCheckedIn(activityId, elderId);
        return count != null && count > 0;
    }







    /**
     * 将实体转换为VO
     */
    private ActivityRegisterVO convertToVO(ActivityRegister register) {
        ActivityRegisterVO vo = new ActivityRegisterVO();
        BeanUtils.copyProperties(register, vo);

        // 设置活动标题
        Activity activity = activityService.getById(register.getActivityId());
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }

        // 设置老人姓名
        User elder = userService.getById(register.getElderId());
        if (elder != null) {
            vo.setElderName(elder.getUsername());
        }

        // 设置报名人姓名
        User registerUser = userService.getById(register.getRegisterUserId());
        if (registerUser != null) {
            vo.setRegisterUserName(registerUser.getUsername());
        }

        // 设置报名类型名称
        vo.setRegisterTypeName(getRegisterTypeName(register.getRegisterType()));

        // 设置是否已签到
        vo.setHasCheckedIn(register.getStatus() == 4);

        // 设置状态名称
        vo.setStatusName(getStatusName(register.getStatus()));

        return vo;
    }




    /**
     * 获取报名类型名称
     */
    private String getRegisterTypeName(Integer registerType) {
        if (registerType == null) {
            return "未知类型";
        }

        return switch (registerType) {
            case 0 -> "老人自己报名";
            case 1 -> "家属代报名";
            default -> "未知类型";
        };
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知状态";
        }

        return switch (status) {
            case 0 -> "待审核";
            case 1 -> "已通过";
            case 2 -> "已拒绝";
            case 3 -> "已取消";
            case 4 -> "已签到";
            default -> "未知状态";
        };
    }

    /**
     * 获取活动报名统计
     */
    @Override
    public Map<String, Object> getRegisterStats(Long activityId) {
        // 检查活动是否存在
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 查询各状态的报名数量
        LambdaQueryWrapper<ActivityRegister> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegister::getActivityId, activityId);
        List<ActivityRegister> registers = list(wrapper);

        // 统计各状态的数量
        int totalCount = registers.size();
        int pendingCount = 0;
        int approvedCount = 0;
        int rejectedCount = 0;
        int cancelledCount = 0;
        int checkedInCount = 0;

        for (ActivityRegister register : registers) {
            switch (register.getStatus()) {
                case 0 -> pendingCount++;
                case 1 -> approvedCount++;
                case 2 -> rejectedCount++;
                case 3 -> cancelledCount++;
                case 4 -> checkedInCount++;
            }
        }

        // 构建统计结果
        Map<String, Object> stats = new HashMap<>();
        stats.put("activityId", activityId);
        stats.put("activityTitle", activity.getTitle());
        stats.put("totalCount", totalCount);
        stats.put("pendingCount", pendingCount);
        stats.put("approvedCount", approvedCount);
        stats.put("rejectedCount", rejectedCount);
        stats.put("cancelledCount", cancelledCount);
        stats.put("checkedInCount", checkedInCount);

        // 添加活动信息
        stats.put("maxParticipants", activity.getMaxParticipants());
        stats.put("remainingSlots", activity.getMaxParticipants() != null ? activity.getMaxParticipants() - approvedCount : null);

        return stats;
    }

    /**
     * 导出活动报名列表
     */
    @Override
    public void exportRegisterList(Long activityId, HttpServletResponse response) {
        try {
            // 检查活动是否存在
            Activity activity = activityService.getById(activityId);
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }

            // 查询活动报名列表
            LambdaQueryWrapper<ActivityRegister> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivityRegister::getActivityId, activityId)
                   .orderByDesc(ActivityRegister::getCreatedAt);
            List<ActivityRegister> registers = list(wrapper);

            // 设置响应头
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = "activity_register_" + activityId + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);

            // TODO: 使用Excel导出工具（如EasyExcel）导出数据
            // 这里需要添加相关依赖并实现导出逻辑
            // 例如：
            // EasyExcel.write(response.getOutputStream(), ActivityRegisterExportVO.class)
            //          .sheet("活动报名列表")
            //          .doWrite(convertToExportVOList(registers));

            // 暂时返回简单的文本信息
            response.getWriter().write("导出功能待实现，需要添加Excel导出相关依赖");
        } catch (IOException e) {
            log.error("导出活动报名列表失败: {}", e.getMessage(), e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }
}
