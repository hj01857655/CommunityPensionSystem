package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.dto.HealthAlertDTO;
import com.communitypension.communitypensionadmin.entity.HealthAlert;
import com.communitypension.communitypensionadmin.entity.HealthMonitor;
import com.communitypension.communitypensionadmin.mapper.HealthAlertMapper;
import com.communitypension.communitypensionadmin.mapper.HealthMonitorMapper;
import com.communitypension.communitypensionadmin.service.HealthAlertService;
import com.communitypension.communitypensionadmin.vo.HealthAlertVO;
import com.communitypension.communitypensionadmin.websocket.WebSocketMessage;
import com.communitypension.communitypensionadmin.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 健康预警服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthAlertServiceImpl extends ServiceImpl<HealthAlertMapper, HealthAlert> implements HealthAlertService {

    private final HealthAlertMapper healthAlertMapper;
    private final HealthMonitorMapper healthMonitorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addHealthAlert(HealthAlertDTO alertDTO) {
        // 参数校验
        if (alertDTO.getElderId() == null) {
            throw new IllegalArgumentException("老人ID不能为空");
        }

        // 设置默认值
        LocalDateTime now = LocalDateTime.now();

        HealthAlert alert = new HealthAlert();
        BeanUtils.copyProperties(alertDTO, alert);

        // 设置默认值
        if (alert.getAlertTime() == null) {
            alert.setAlertTime(now);
        }
        if (alert.getStatus() == null) {
            alert.setStatus(0); // 默认未处理
        }
        if (alert.getNotified() == null) {
            alert.setNotified(0); // 默认未通知
        }

        alert.setCreateTime(now);
        alert.setUpdateTime(now);

        // 保存到数据库
        boolean success = save(alert);
        if (!success) {
            throw new RuntimeException("添加健康预警失败");
        }

        // 是否需要立即发送通知
        if (Boolean.TRUE.equals(alertDTO.getSendNotification())) {
            sendAlertNotification(alert.getId());
        }

        return alert.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHealthAlert(HealthAlertDTO alertDTO) {
        // 参数校验
        if (alertDTO.getId() == null) {
            throw new IllegalArgumentException("预警ID不能为空");
        }

        // 查询原记录
        HealthAlert existingAlert = getById(alertDTO.getId());
        if (existingAlert == null) {
            throw new IllegalArgumentException("预警记录不存在");
        }

        // 更新记录
        HealthAlert alert = new HealthAlert();
        BeanUtils.copyProperties(alertDTO, alert);
        alert.setUpdateTime(LocalDateTime.now());

        boolean success = updateById(alert);

        // 是否需要立即发送通知
        if (success && Boolean.TRUE.equals(alertDTO.getSendNotification())) {
            sendAlertNotification(alert.getId());
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHealthAlert(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("预警ID不能为空");
        }

        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleHealthAlert(Long id, Long handlerId, String handleNote, Integer status) {
        if (id == null) {
            throw new IllegalArgumentException("预警ID不能为空");
        }
        if (handlerId == null) {
            throw new IllegalArgumentException("处理人ID不能为空");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new IllegalArgumentException("处理状态无效");
        }

        // 查询原记录
        HealthAlert existingAlert = getById(id);
        if (existingAlert == null) {
            throw new IllegalArgumentException("预警记录不存在");
        }

        // 更新处理信息
        HealthAlert alert = new HealthAlert();
        alert.setId(id);
        alert.setStatus(status);
        alert.setHandlerId(handlerId);
        alert.setHandleTime(LocalDateTime.now());
        alert.setHandleNote(handleNote);
        alert.setUpdateTime(LocalDateTime.now());

        return updateById(alert);
    }

    @Override
    public HealthAlertVO getHealthAlertDetail(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("预警ID不能为空");
        }

        return healthAlertMapper.selectHealthAlertDetail(id);
    }

    @Override
    public Page<HealthAlertVO> getHealthAlertList(
            Long elderId,
            String elderName,
            Integer alertType,
            Integer alertLevel,
            Integer status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer pageNum,
            Integer pageSize) {

        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<HealthAlertVO> page = new Page<>(pageNum, pageSize);
        IPage<HealthAlertVO> resultPage = healthAlertMapper.selectHealthAlertPage(
                page, elderId, elderName, alertType, alertLevel, status, startTime, endTime);
        Page<HealthAlertVO> result = new Page<>();
        BeanUtils.copyProperties(resultPage, result);
        return result;
    }

    @Override
    public int getUnhandledAlertsCount(Long elderId) {
        if (elderId == null) {
            throw new IllegalArgumentException("老人ID不能为空");
        }

        return healthAlertMapper.countUnhandledAlertsByElderId(elderId);
    }

    @Override
    public List<HealthAlertVO> getRecentAlerts(Long elderId, int limit) {
        if (elderId == null) {
            throw new IllegalArgumentException("老人ID不能为空");
        }
        if (limit <= 0) {
            limit = 5; // 默认取5条
        }

        return healthAlertMapper.selectRecentAlertsByElderId(elderId, limit);
    }

    @Override
    public List<HealthAlertVO> getUnhandledEmergencyAlerts() {
        return healthAlertMapper.selectUnhandledEmergencyAlerts();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateAlertFromMonitor(Long monitorId) {
        if (monitorId == null) {
            throw new IllegalArgumentException("监测ID不能为空");
        }

        // 获取健康监测记录
        HealthMonitor monitor = healthMonitorMapper.selectById(monitorId);
        if (monitor == null) {
            throw new IllegalArgumentException("健康监测记录不存在");
        }

        // 检查是否需要生成预警
        Integer alertType = null;
        Integer alertLevel = null;
        String title = null;
        String content = null;

        // 根据监测类型和数据进行判断
        // 此处简单演示，实际应根据具体业务逻辑进行预警判断
        // 1: 血压 2: 血糖 3: 心率 4: 体温 等

        switch (monitor.getMonitoringType()) {
            case "1": // 血压监测
                // 解析血压值（格式为"收缩压/舒张压"，例如"120/80"）
                String[] pressureValues = monitor.getMonitoringValue().split("/");
                if (pressureValues.length >= 2) {
                    try {
                        int systolicPressure = Integer.parseInt(pressureValues[0].trim());
                        int diastolicPressure = Integer.parseInt(pressureValues[1].trim());

                        if (systolicPressure > 140) {
                            alertType = 1; // 血压异常
                            alertLevel = systolicPressure > 180 ? 4 : (systolicPressure > 160 ? 3 : 2);
                            title = "高血压预警";
                            content = "测量到收缩压为" + systolicPressure + "mmHg，超过正常范围。";
                        } else if (diastolicPressure > 90) {
                            alertType = 1; // 血压异常
                            alertLevel = diastolicPressure > 110 ? 4 : (diastolicPressure > 100 ? 3 : 2);
                            title = "高血压预警";
                            content = "测量到舒张压为" + diastolicPressure + "mmHg，超过正常范围。";
                        }
                    } catch (NumberFormatException e) {
                        log.error("解析血压值失败：" + monitor.getMonitoringValue(), e);
                    }
                }
                break;

            case "2": // 血糖监测
                try {
                    double bloodSugar = Double.parseDouble(monitor.getMonitoringValue());
                    if (bloodSugar > 11.1) {
                        alertType = 2; // 血糖异常
                        alertLevel = bloodSugar > 16.7 ? 4 : (bloodSugar > 13.9 ? 3 : 2);
                        title = "高血糖预警";
                        content = "测量到血糖为" + bloodSugar + "mmol/L，超过正常范围。";
                    } else if (bloodSugar < 3.9) {
                        alertType = 2; // 血糖异常
                        alertLevel = bloodSugar < 2.8 ? 4 : (bloodSugar < 3.3 ? 3 : 2);
                        title = "低血糖预警";
                        content = "测量到血糖为" + bloodSugar + "mmol/L，低于正常范围。";
                    }
                } catch (NumberFormatException e) {
                    log.error("解析血糖值失败：" + monitor.getMonitoringValue(), e);
                }
                break;

            case "3": // 心率监测
                try {
                    int heartRate = Integer.parseInt(monitor.getMonitoringValue());
                    if (heartRate > 100) {
                        alertType = 3; // 心率异常
                        alertLevel = heartRate > 120 ? 4 : (heartRate > 110 ? 3 : 2);
                        title = "心率过快预警";
                        content = "测量到心率为" + heartRate + "次/分钟，高于正常范围。";
                    } else if (heartRate < 60) {
                        alertType = 3; // 心率异常
                        alertLevel = heartRate < 45 ? 4 : (heartRate < 50 ? 3 : 2);
                        title = "心率过慢预警";
                        content = "测量到心率为" + heartRate + "次/分钟，低于正常范围。";
                    }
                } catch (NumberFormatException e) {
                    log.error("解析心率值失败：" + monitor.getMonitoringValue(), e);
                }
                break;

            case "4": // 体温监测
                try {
                    double bodyTemperature = Double.parseDouble(monitor.getMonitoringValue());
                    if (bodyTemperature > 37.5) {
                        alertType = 4; // 体温异常
                        alertLevel = bodyTemperature > 39 ? 4 : (bodyTemperature > 38.5 ? 3 : 2);
                        title = "体温过高预警";
                        content = "测量到体温为" + bodyTemperature + "℃，高于正常范围。";
                    } else if (bodyTemperature < 36) {
                        alertType = 4; // 体温异常
                        alertLevel = bodyTemperature < 35 ? 4 : (bodyTemperature < 35.5 ? 3 : 2);
                        title = "体温过低预警";
                        content = "测量到体温为" + bodyTemperature + "℃，低于正常范围。";
                    }
                } catch (NumberFormatException e) {
                    log.error("解析体温值失败：" + monitor.getMonitoringValue(), e);
                }
                break;

            default:
                break;
        }

        // 如果需要生成预警
        if (alertType != null && alertLevel != null && title != null && content != null) {
            HealthAlertDTO alertDTO = HealthAlertDTO.builder()
                    .elderId(monitor.getElderId())
                    .alertType(alertType)
                    .alertLevel(alertLevel)
                    .alertSource(1) // 系统自动监测
                    .title(title)
                    .content(content)
                    .alertTime(LocalDateTime.now())
                    .status(0) // 未处理
                    .relatedDataId(monitorId)
                    .sendNotification(true) // 立即发送通知
                    .build();

            return addHealthAlert(alertDTO);
        }

        return null; // 不需要生成预警
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddHealthAlerts(List<HealthAlertDTO> alertDTOs) {
        if (alertDTOs == null || alertDTOs.isEmpty()) {
            return false;
        }

        List<Long> alertIds = new ArrayList<>();
        for (HealthAlertDTO alertDTO : alertDTOs) {
            try {
                Long alertId = addHealthAlert(alertDTO);
                if (alertId != null) {
                    alertIds.add(alertId);
                }
            } catch (Exception e) {
                log.error("批量添加健康预警时出错", e);
            }
        }

        return !alertIds.isEmpty();
    }

    @Override
    public boolean sendAlertNotification(Long alertId) {
        if (alertId == null) {
            throw new IllegalArgumentException("预警ID不能为空");
        }

        // 查询预警详情
        HealthAlertVO alert = getHealthAlertDetail(alertId);
        if (alert == null) {
            throw new IllegalArgumentException("预警记录不存在");
        }

        try {
            // 构建WebSocket消息
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(WebSocketMessage.MessageType.HEALTH)
                    .title(alert.getTitle())
                    .content(alert.getContent())
                    .timestamp(LocalDateTime.now())
                    .messageId(alertId)
                    .source("health_alert")
                    .link("/health/alert/detail/" + alertId)
                    .build();

            // 发送消息给老人
            boolean sent = false;
            if (alert.getElderId() != null && WebSocketServer.isOnline(alert.getElderId())) {
                WebSocketServer.sendMessage(alert.getElderId(), message);
                sent = true;
            }

            // 向管理员广播紧急预警
            if (Objects.equals(alert.getAlertLevel(), 4)) {
                WebSocketServer.broadcastMessage(message);
                sent = true;
            }

            // 更新通知状态
            if (sent) {
                HealthAlert updateAlert = new HealthAlert();
                updateAlert.setId(alertId);
                updateAlert.setNotified(1);
                updateAlert.setNotifyTime(LocalDateTime.now());
                updateAlert.setUpdateTime(LocalDateTime.now());
                updateById(updateAlert);
            }

            return sent;
        } catch (Exception e) {
            log.error("发送健康预警通知失败", e);
            return false;
        }
    }
} 