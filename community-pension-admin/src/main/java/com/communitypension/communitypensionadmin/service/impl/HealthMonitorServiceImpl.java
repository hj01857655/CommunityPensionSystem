package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.converter.HealthMonitorConverter;
import com.communitypension.communitypensionadmin.dto.HealthMonitorDTO;
import com.communitypension.communitypensionadmin.entity.HealthMonitor;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.HealthMonitorMapper;
import com.communitypension.communitypensionadmin.service.HealthAlertService;
import com.communitypension.communitypensionadmin.service.HealthMonitorService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.vo.HealthMonitorVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 健康监测Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HealthMonitorServiceImpl extends ServiceImpl<HealthMonitorMapper, HealthMonitor> implements HealthMonitorService {

    private final UserService userService;
    private final HealthAlertService healthAlertService;

    /**
     * 添加健康监测记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addHealthMonitor(HealthMonitorDTO monitorDTO) {
        // 检查老人是否存在
        User elder = userService.getById(monitorDTO.getElderId());
        if (elder == null) {
            throw new BusinessException("老人不存在");
        }

        // 验证监测数据
        validateMonitorData(monitorDTO);

        // 转换为实体并保存
        HealthMonitor monitor = HealthMonitorConverter.toEntity(monitorDTO);
        save(monitor);

        // 检查是否需要生成健康预警
        try {
            Long alertId = healthAlertService.generateAlertFromMonitor(monitor.getId());
            if (alertId != null) {
                log.info("基于监测记录自动生成健康预警，监测ID: {}, 预警ID: {}", monitor.getId(), alertId);
            }
        } catch (Exception e) {
            log.error("生成健康预警失败，监测ID: {}, 错误: {}", monitor.getId(), e.getMessage(), e);
            // 预警生成失败不影响监测记录的保存
        }

        return monitor.getId();
    }

    /**
     * 更新健康监测记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHealthMonitor(HealthMonitorDTO monitorDTO) {
        // 检查监测记录是否存在
        HealthMonitor monitor = getById(monitorDTO.getId());
        if (monitor == null) {
            throw new BusinessException("健康监测记录不存在");
        }

        // 检查老人是否存在
        User elder = userService.getById(monitorDTO.getElderId());
        if (elder == null) {
            throw new BusinessException("老人不存在");
        }

        // 验证监测数据
        validateMonitorData(monitorDTO);

        // 更新监测记录
        HealthMonitor updateMonitor = HealthMonitorConverter.toEntity(monitorDTO);
        return updateById(updateMonitor);
    }

    /**
     * 删除健康监测记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHealthMonitor(Long id) {
        // 检查监测记录是否存在
        HealthMonitor monitor = getById(id);
        if (monitor == null) {
            throw new BusinessException("健康监测记录不存在");
        }

        return removeById(id);
    }

    /**
     * 获取健康监测详情
     */
    @Override
    public HealthMonitorVO getHealthMonitorDetail(Long id) {
        // 获取监测记录
        HealthMonitor monitor = getById(id);
        if (monitor == null) {
            throw new BusinessException("健康监测记录不存在");
        }

        // 获取老人信息
        User elder = userService.getById(monitor.getElderId());

        // 转换为VO
        return HealthMonitorConverter.toVO(monitor, elder, null);
    }

    /**
     * 分页查询健康监测列表
     */
    @Override
    public Page<HealthMonitorVO> getHealthMonitorList(Long elderId, String elderName, Integer monitorType,
                                                     LocalDate startDate, LocalDate endDate,
                                                     Integer pageNum, Integer pageSize) {
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
                Page<HealthMonitorVO> emptyPage = new Page<>(pageNum, pageSize, 0);
                emptyPage.setRecords(new ArrayList<>());
                return emptyPage;
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<HealthMonitor> wrapper = new LambdaQueryWrapper<>();
        if (elderId != null) {
            wrapper.eq(HealthMonitor::getElderId, elderId);
        } else if (!elderIds.isEmpty()) {
            wrapper.in(HealthMonitor::getElderId, elderIds);
        }
        if (monitorType != null) {
            wrapper.eq(HealthMonitor::getMonitoringType, String.valueOf(monitorType));
        }
        if (startDate != null) {
            wrapper.ge(HealthMonitor::getMonitoringTime, LocalDateTime.of(startDate, LocalTime.MIN));
        }
        if (endDate != null) {
            wrapper.le(HealthMonitor::getMonitoringTime, LocalDateTime.of(endDate, LocalTime.MAX));
        }
        // 查询未处理的监测记录
        wrapper.eq(HealthMonitor::getIsProcessed, 0);
        wrapper.orderByDesc(HealthMonitor::getMonitoringTime);

        // 分页查询
        Page<HealthMonitor> page = new Page<>(pageNum, pageSize);
        page = page(page, wrapper);

        // 获取老人ID列表
        List<Long> monitorElderIds = page.getRecords().stream()
                .map(HealthMonitor::getElderId)
                .distinct()
                .collect(Collectors.toList());

        // 获取老人信息
        Map<Long, User> elderMap = new HashMap<>();
        if (!monitorElderIds.isEmpty()) {
            List<User> elders = userService.listByIds(monitorElderIds);
            elderMap = elders.stream().collect(Collectors.toMap(User::getUserId, user -> user));
        }

        // 转换为VO
        List<HealthMonitorVO> voList = HealthMonitorConverter.toVOList(page.getRecords(), elderMap);

        // 构建返回结果
        Page<HealthMonitorVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    /**
     * 获取老人的最新健康监测记录
     */
    @Override
    public List<HealthMonitorVO> getElderLatestMonitors(Long elderId) {
        // 检查老人是否存在
        User elder = userService.getById(elderId);
        if (elder == null) {
            throw new BusinessException("老人不存在");
        }

        // 查询每种类型的最新监测记录
        List<HealthMonitor> latestMonitors = new ArrayList<>();
        for (int type = 1; type <= 7; type++) {
            LambdaQueryWrapper<HealthMonitor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HealthMonitor::getElderId, elderId);
            wrapper.eq(HealthMonitor::getMonitoringType, String.valueOf(type));
            wrapper.eq(HealthMonitor::getIsProcessed, false);
            wrapper.orderByDesc(HealthMonitor::getMonitoringTime);
            wrapper.last("LIMIT 1");
            HealthMonitor monitor = getOne(wrapper);
            if (monitor != null) {
                latestMonitors.add(monitor);
            }
        }

        // 转换为VO
        Map<Long, User> elderMap = new HashMap<>();
        elderMap.put(elderId, elder);
        return HealthMonitorConverter.toVOList(latestMonitors, elderMap);
    }

    /**
     * 获取老人的健康监测统计
     */
    @Override
    public Map<String, Object> getElderMonitorStats(Long elderId, Integer monitorType, LocalDate startDate, LocalDate endDate) {
        // 检查老人是否存在
        User elder = userService.getById(elderId);
        if (elder == null) {
            throw new BusinessException("老人不存在");
        }

        // 构建查询条件
        LambdaQueryWrapper<HealthMonitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthMonitor::getElderId, elderId);
        if (monitorType != null) {
            wrapper.eq(HealthMonitor::getMonitoringType, String.valueOf(monitorType));
        }
        if (startDate != null) {
            wrapper.ge(HealthMonitor::getMonitoringTime, LocalDateTime.of(startDate, LocalTime.MIN));
        }
        if (endDate != null) {
            wrapper.le(HealthMonitor::getMonitoringTime, LocalDateTime.of(endDate, LocalTime.MAX));
        }
        wrapper.eq(HealthMonitor::getIsProcessed, false);
        wrapper.orderByAsc(HealthMonitor::getMonitoringTime);

        // 查询监测记录
        List<HealthMonitor> monitors = list(wrapper);

        // 统计数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", monitors.size());

        // 按监测结果统计
        Map<String, Long> resultStats = monitors.stream()
                .collect(Collectors.groupingBy(HealthMonitor::getMonitoringStatus, Collectors.counting()));
        stats.put("resultStats", resultStats);

        // 按监测类型统计
        Map<String, Long> typeStats = monitors.stream()
                .collect(Collectors.groupingBy(HealthMonitor::getMonitoringType, Collectors.counting()));
        stats.put("typeStats", typeStats);

        // 按日期统计
        Map<String, Long> dateStats = monitors.stream()
                .collect(Collectors.groupingBy(
                        monitor -> monitor.getMonitoringTime().toLocalDate().toString(),
                        Collectors.counting()));
        stats.put("dateStats", dateStats);

        // 计算平均值、最大值、最小值等
        if (monitorType != null) {
            switch (monitorType) {
                case 1: // 血压
                    DoubleSummaryStatistics systolicStats = monitors.stream()
                            .filter(m -> getNumericValue(m.getMonitoringValue(), "systolic") != null)
                            .mapToDouble(m -> getNumericValue(m.getMonitoringValue(), "systolic"))
                            .summaryStatistics();
                    DoubleSummaryStatistics diastolicStats = monitors.stream()
                            .filter(m -> getNumericValue(m.getMonitoringValue(), "diastolic") != null)
                            .mapToDouble(m -> getNumericValue(m.getMonitoringValue(), "diastolic"))
                            .summaryStatistics();
                    stats.put("systolicStats", systolicStats);
                    stats.put("diastolicStats", diastolicStats);
                    break;
                case 2: // 血糖
                    DoubleSummaryStatistics bloodSugarStats = monitors.stream()
                            .filter(m -> getNumericValue(m.getMonitoringValue(), null) != null)
                            .mapToDouble(m -> getNumericValue(m.getMonitoringValue(), null))
                            .summaryStatistics();
                    stats.put("bloodSugarStats", bloodSugarStats);
                    break;
                case 3: // 体温
                    DoubleSummaryStatistics temperatureStats = monitors.stream()
                            .filter(m -> getNumericValue(m.getMonitoringValue(), null) != null)
                            .mapToDouble(m -> getNumericValue(m.getMonitoringValue(), null))
                            .summaryStatistics();
                    stats.put("temperatureStats", temperatureStats);
                    break;
                case 4: // 心率
                    DoubleSummaryStatistics heartRateStats = monitors.stream()
                            .filter(m -> getNumericValue(m.getMonitoringValue(), null) != null)
                            .mapToDouble(m -> getNumericValue(m.getMonitoringValue(), null))
                            .summaryStatistics();
                    stats.put("heartRateStats", heartRateStats);
                    break;
                case 5: // 血氧
                    DoubleSummaryStatistics bloodOxygenStats = monitors.stream()
                            .filter(m -> getNumericValue(m.getMonitoringValue(), null) != null)
                            .mapToDouble(m -> getNumericValue(m.getMonitoringValue(), null))
                            .summaryStatistics();
                    stats.put("bloodOxygenStats", bloodOxygenStats);
                    break;
                case 6: // 体重
                    DoubleSummaryStatistics weightStats = monitors.stream()
                            .filter(m -> getNumericValue(m.getMonitoringValue(), null) != null)
                            .mapToDouble(m -> getNumericValue(m.getMonitoringValue(), null))
                            .summaryStatistics();
                    stats.put("weightStats", weightStats);
                    break;
            }
        }

        return stats;
    }

    /**
     * 批量添加健康监测记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddHealthMonitors(List<HealthMonitorDTO> monitorDTOs) {
        if (monitorDTOs == null || monitorDTOs.isEmpty()) {
            return false;
        }

        List<HealthMonitor> monitors = new ArrayList<>();
        for (HealthMonitorDTO dto : monitorDTOs) {
            // 检查老人是否存在
            User elder = userService.getById(dto.getElderId());
            if (elder == null) {
                throw new BusinessException("老人不存在，ID: " + dto.getElderId());
            }

            // 验证监测数据
            validateMonitorData(dto);

            // 转换为实体
            HealthMonitor monitor = HealthMonitorConverter.toEntity(dto);
            monitors.add(monitor);
        }

        boolean success = saveBatch(monitors);
        
        // 为每条监测记录检查是否需要生成健康预警
        if (success) {
            for (HealthMonitor monitor : monitors) {
                try {
                    Long alertId = healthAlertService.generateAlertFromMonitor(monitor.getId());
                    if (alertId != null) {
                        log.info("基于监测记录自动生成健康预警，监测ID: {}, 预警ID: {}", monitor.getId(), alertId);
                    }
                } catch (Exception e) {
                    log.error("生成健康预警失败，监测ID: {}, 错误: {}", monitor.getId(), e.getMessage(), e);
                    // 预警生成失败不影响其他记录的处理
                }
            }
        }

        return success;
    }

    /**
     * 验证监测数据
     */
    private void validateMonitorData(HealthMonitorDTO monitorDTO) {
        String monitorType = monitorDTO.getMonitoringType();
        if (monitorType == null || monitorType.isEmpty()) {
            throw new BusinessException("监测类型不能为空");
        }

        if (monitorDTO.getMonitoringValue() == null || monitorDTO.getMonitoringValue().isEmpty()) {
            throw new BusinessException("监测值不能为空");
        }

        switch (monitorType) {
            case "1": // 血压
                if (!monitorDTO.getMonitoringValue().contains("/")) {
                    throw new BusinessException("血压监测值格式不正确，应为收缩压/舒张压");
                }
                break;
            case "2": // 血糖
                // 验证血糖值是否为数字
                try {
                    Double.parseDouble(monitorDTO.getMonitoringValue());
                } catch (NumberFormatException e) {
                    throw new BusinessException("血糖值必须为数字");
                }
                break;
            case "3": // 体温
                // 验证体温值是否为数字
                try {
                    Double.parseDouble(monitorDTO.getMonitoringValue());
                } catch (NumberFormatException e) {
                    throw new BusinessException("体温值必须为数字");
                }
                break;
            case "4": // 心率
                // 验证心率值是否为整数
                try {
                    Integer.parseInt(monitorDTO.getMonitoringValue());
                } catch (NumberFormatException e) {
                    throw new BusinessException("心率值必须为整数");
                }
                break;
            case "5": // 血氧
                // 验证血氧值是否为整数且在0-100之间
                try {
                    int value = Integer.parseInt(monitorDTO.getMonitoringValue());
                    if (value < 0 || value > 100) {
                        throw new BusinessException("血氧饱和度必须在0-100之间");
                    }
                } catch (NumberFormatException e) {
                    throw new BusinessException("血氧饱和度必须为整数");
                }
                break;
            case "6": // 体重
                // 验证体重值是否为数字
                try {
                    Double.parseDouble(monitorDTO.getMonitoringValue());
                } catch (NumberFormatException e) {
                    throw new BusinessException("体重值必须为数字");
                }
                break;
            case "7": // 其他
                // 其他类型不做特殊验证
                break;
            default:
                throw new BusinessException("无效的监测类型");
        }

        if (monitorDTO.getMonitoringStatus() == null || monitorDTO.getMonitoringStatus().isEmpty()) {
            throw new BusinessException("监测状态不能为空");
        }
    }

    /**
     * 从监测值中提取数值
     *
     * @param monitoringValue 监测值字符串
     * @param valueType 值类型，如"systolic"或"diastolic"，用于血压等复合值
     * @return 数值，如果无法解析则返回null
     */
    private Double getNumericValue(String monitoringValue, String valueType) {
        if (monitoringValue == null || monitoringValue.isEmpty()) {
            return null;
        }

        try {
            // 如果是血压值，需要分离收缩压和舒张压
            if ("systolic".equals(valueType) && monitoringValue.contains("/")) {
                String[] parts = monitoringValue.split("/");
                if (parts.length >= 1) {
                    return Double.parseDouble(parts[0].trim());
                }
            } else if ("diastolic".equals(valueType) && monitoringValue.contains("/")) {
                String[] parts = monitoringValue.split("/");
                if (parts.length >= 2) {
                    return Double.parseDouble(parts[1].trim());
                }
            } else {
                // 其他类型直接解析
                return Double.parseDouble(monitoringValue.trim());
            }
        } catch (NumberFormatException e) {
            // 如果无法解析为数字，返回null
            return null;
        }

        return null;
    }
}
