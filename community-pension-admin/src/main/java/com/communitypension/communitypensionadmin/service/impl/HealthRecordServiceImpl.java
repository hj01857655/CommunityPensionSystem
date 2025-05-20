package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.converter.HealthRecordConverter;
import com.communitypension.communitypensionadmin.pojo.dto.HealthRecordDTO;
import com.communitypension.communitypensionadmin.entity.HealthRecord;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.HealthRecordMapper;
import com.communitypension.communitypensionadmin.service.HealthRecordService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.pojo.vo.HealthRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.communitypension.communitypensionadmin.utils.ExcelExporter;
import com.communitypension.communitypensionadmin.utils.SecurityUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

/**
 * 健康档案Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    private final UserService userService;

    /**
     * 添加健康档案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addHealthRecord(HealthRecordDTO recordDTO) {
        // 检查老人是否存在
        User elder = userService.getById(recordDTO.getElderId());
        if (elder == null) {
            throw new BusinessException("老人不存在");
        }

        // 设置记录时间和记录类型
        if (recordDTO.getRecordTime() == null) {
            recordDTO.setRecordTime(LocalDateTime.now());
        }
        if (!StringUtils.hasText(recordDTO.getRecordType())) {
            recordDTO.setRecordType("常规记录");
        }
        
        // 设置记录人ID（如果未设置）
        if (recordDTO.getRecorderId() == null) {
            // 从当前登录用户中获取ID
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                // 如果无法获取当前用户，则使用默认值或抛出异常
                throw new BusinessException("无法获取当前用户信息，请重新登录");
            }
            recordDTO.setRecorderId(currentUserId);
        }

        // 转换为实体并保存
        HealthRecord record = HealthRecordConverter.toEntity(recordDTO);
        save(record);

        return record.getId();
    }

    /**
     * 更新健康档案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHealthRecord(HealthRecordDTO recordDTO) {
        // 检查档案是否存在
        HealthRecord record = getById(recordDTO.getId());
        if (record == null) {
            throw new BusinessException("健康档案不存在");
        }

        // 检查老人是否存在
        User elder = userService.getById(recordDTO.getElderId());
        if (elder == null) {
            throw new BusinessException("老人不存在");
        }

        // 设置记录时间
        if (recordDTO.getRecordTime() == null) {
            recordDTO.setRecordTime(LocalDateTime.now());
        }

        // 更新档案
        HealthRecord updateRecord = HealthRecordConverter.toEntity(recordDTO);
        return updateById(updateRecord);
    }

    /**
     * 删除健康档案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHealthRecord(Long id) {
        // 检查档案是否存在
        HealthRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("健康档案不存在");
        }

        return removeById(id);
    }

    /**
     * 获取健康档案详情
     */
    @Override
    public HealthRecordVO getHealthRecordDetail(Long id) {
        // 获取档案
        HealthRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("健康档案不存在");
        }

        // 获取老人信息
        User elder = userService.getById(record.getElderId());

        // 获取记录人信息
        User recorder = null;
        if (record.getRecorderId() != null) {
            recorder = userService.getById(record.getRecorderId());
        }

        // 转换为VO
        return HealthRecordConverter.toVO(record, elder, recorder);
    }

    /**
     * 获取老人的健康档案
     */
    @Override
    public HealthRecordVO getElderHealthRecord(Long elderId) {
        // 检查老人是否存在
        User elder = userService.getById(elderId);
        if (elder == null) {
            throw new BusinessException("老人不存在");
        }

        // 查询最新的健康档案
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getElderId, elderId);
        wrapper.orderByDesc(HealthRecord::getRecordTime);
        wrapper.last("LIMIT 1");
        HealthRecord record = getOne(wrapper);

        if (record == null) {
            return null;
        }

        // 获取记录人信息
        User recorder = null;
        if (record.getRecorderId() != null) {
            recorder = userService.getById(record.getRecorderId());
        }

        // 转换为VO
        return HealthRecordConverter.toVO(record, elder, recorder);
    }

    /**
     * 分页查询健康档案列表
     */
    @Override
    public Page<HealthRecordVO> getHealthRecordList(String elderName, Integer pageNum, Integer pageSize) {
        // 如果有老人姓名，先查询老人ID列表
        List<Long> elderIds = new ArrayList<>();
        if (StringUtils.hasText(elderName)) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getUsername, elderName);
            userWrapper.eq(User::getIsActive, 0);
            List<User> users = userService.list(userWrapper);
            elderIds = users.stream().map(User::getUserId).collect(Collectors.toList());
            if (elderIds.isEmpty()) {
                // 没有找到符合条件的老人，返回空结果
                Page<HealthRecordVO> emptyPage = new Page<>(pageNum, pageSize, 0);
                emptyPage.setRecords(new ArrayList<>());
                return emptyPage;
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        if (!elderIds.isEmpty()) {
            wrapper.in(HealthRecord::getElderId, elderIds);
        }
        wrapper.orderByDesc(HealthRecord::getRecordTime);

        // 分页查询
        Page<HealthRecord> page = new Page<>(pageNum, pageSize);
        page = page(page, wrapper);

        // 获取老人ID列表
        List<Long> recordElderIds = page.getRecords().stream()
                .map(HealthRecord::getElderId)
                .collect(Collectors.toList());

        // 获取记录人ID列表
        List<Long> recorderIds = page.getRecords().stream()
                .map(HealthRecord::getRecorderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 获取老人信息
        Map<Long, User> elderMap = new HashMap<>();
        if (!recordElderIds.isEmpty()) {
            List<User> elders = userService.listByIds(recordElderIds);
            elderMap = elders.stream().collect(Collectors.toMap(User::getUserId, user -> user));
        }

        // 获取记录人信息
        Map<Long, User> userMap = new HashMap<>();
        if (!recorderIds.isEmpty()) {
            List<User> recorders = userService.listByIds(recorderIds);
            userMap = recorders.stream().collect(Collectors.toMap(User::getUserId, user -> user));
        }

        // 转换为VO
        List<HealthRecordVO> voList = HealthRecordConverter.toVOList(page.getRecords(), elderMap, userMap);

        // 构建返回结果
        Page<HealthRecordVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    public byte[] exportHealthRecords() {
        try {
            // 获取所有健康档案
            List<HealthRecord> records = list();
            
            // 直接返回导出的数据
            return ExcelExporter.exportToExcel(records);
        } catch (IOException e) {
            // 处理异常，例如记录日志或返回错误信息
            log.error("导出健康档案时发生错误", e);
            return new byte[0]; // 返回空字节数组或根据需要处理
        }
    }
}
