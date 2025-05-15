package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.entity.WarningRecord;
import com.communitypension.communitypensionadmin.mapper.WarningRecordMapper;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.service.WarningRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预警记录服务实现类
 */
@Service
@RequiredArgsConstructor
public class WarningRecordServiceImpl extends ServiceImpl<WarningRecordMapper, WarningRecord> implements WarningRecordService {

    private final UserService userService;

    @Override
    public long count() {
        return count(null);
    }

    @Override
    public List<Map<String, Object>> getRecentWarnings(int limit) {
        // 查询最新的预警记录
        LambdaQueryWrapper<WarningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WarningRecord::getWarningTime);
        wrapper.last("LIMIT " + limit);
        List<WarningRecord> warningRecords = list(wrapper);
        
        // 获取所有涉及的用户ID
        List<Long> userIds = warningRecords.stream()
                .map(WarningRecord::getUserId)
                .collect(Collectors.toList());
        
        // 批量查询用户信息
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getUserId, user -> user));
        }
        
        // 转换为前端所需格式
        Map<Long, User> finalUserMap = userMap;
        return warningRecords.stream().map(warning -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", warning.getId());
            
            // 获取用户名称
            User user = finalUserMap.get(warning.getUserId());
            map.put("userName", user != null ? user.getName() : "未知用户");
            
            // 设置其他字段
            map.put("warningType", warning.getWarningType());
            map.put("warningMessage", warning.getWarningMessage());
            map.put("warningTime", warning.getWarningTime());
            return map;
        }).collect(Collectors.toList());
    }
} 