package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.communitypension.communitypensionadmin.entity.EmergencyCall;
import com.communitypension.communitypensionadmin.query.EmergencyCallQuery;
import com.communitypension.communitypensionadmin.service.EmergencyCallService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 紧急呼叫控制器
 */
@RestController
@RequestMapping("/api/emergency")
public class EmergencyCallController {

    @Autowired
    private EmergencyCallService emergencyCallService;

    // 注入WebSocketServer
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 发送紧急呼叫
     *
     * @param emergencyCall 紧急呼叫信息
     * @return 处理结果
     */
    @PostMapping("/call")
    public Result<EmergencyCall> sendEmergencyCall(@RequestBody EmergencyCall emergencyCall) {
        boolean success = emergencyCallService.saveEmergencyCall(emergencyCall);
        if (success) {
            // 广播紧急呼叫消息给所有在线管理员
            try {
                // 创建紧急消息对象
                java.util.Map<String, Object> emergencyMessage = new java.util.HashMap<>();
                emergencyMessage.put("type", "EMERGENCY");
                emergencyMessage.put("title", "紧急呼叫通知");
                emergencyMessage.put("content", emergencyCall.getDescription());
                emergencyMessage.put("callId", emergencyCall.getId());
                emergencyMessage.put("userId", emergencyCall.getUserId());
                emergencyMessage.put("userName", emergencyCall.getUserName());
                emergencyMessage.put("userPhone", emergencyCall.getUserPhone());
                emergencyMessage.put("location", emergencyCall.getLocation());
                emergencyMessage.put("callTime", emergencyCall.getCallTime());
                emergencyMessage.put("status", emergencyCall.getStatus());

                // 广播紧急消息给所有在线管理员
                com.communitypension.communitypensionadmin.websocket.WebSocketServer.broadcastEmergencyMessage(emergencyMessage);

                // 记录日志
                org.slf4j.LoggerFactory.getLogger(this.getClass()).info("紧急呼叫消息已广播，ID: {}", emergencyCall.getId());
            } catch (Exception e) {
                // 广播失败不影响主流程
                org.slf4j.LoggerFactory.getLogger(this.getClass()).error("广播紧急呼叫消息失败", e);
            }

            return Result.success("紧急呼叫已发送", emergencyCall);
        } else {
            return Result.error("紧急呼叫发送失败");
        }
    }

    /**
     * 获取紧急呼叫历史记录
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/history")
    public Result<IPage<EmergencyCall>> getEmergencyCallHistory(EmergencyCallQuery query) {
        // 从请求参数或认证信息中获取用户ID
        Long userId = query.getUserId();
        IPage<EmergencyCall> page = emergencyCallService.getUserEmergencyCallHistory(userId, query);
        return Result.success("查询成功", page);
    }

    /**
     * 取消紧急呼叫
     *
     * @param callId 呼叫ID
     * @return 处理结果
     */
    @PutMapping("/cancel/{callId}")
    public Result<Void> cancelEmergencyCall(@PathVariable Long callId) {
        boolean success = emergencyCallService.cancelEmergencyCall(callId);
        if (success) {
            return Result.success("紧急呼叫已取消");
        } else {
            return Result.error("紧急呼叫取消失败，可能已被处理或不存在");
        }
    }

    /**
     * 获取紧急呼叫列表（管理员使用）
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<EmergencyCall>> list(EmergencyCallQuery query) {
        IPage<EmergencyCall> page = emergencyCallService.getEmergencyCallList(query);
        return Result.success("查询成功", page);
    }

    /**
     * 获取紧急呼叫详情
     *
     * @param id 紧急呼叫ID
     * @return 紧急呼叫详情
     */
    @GetMapping("/{id}")
    public Result<EmergencyCall> getInfo(@PathVariable Long id) {
        EmergencyCall emergencyCall = emergencyCallService.getEmergencyCallById(id);
        if (emergencyCall != null) {
            return Result.success("查询成功", emergencyCall);
        } else {
            return Result.error("紧急呼叫不存在");
        }
    }

    /**
     * 处理紧急呼叫
     *
     * @param id          紧急呼叫ID
     * @param processedBy 处理人
     * @return 处理结果
     */
    @PutMapping("/process/{id}")
    public Result<Void> processEmergencyCall(@PathVariable Long id, @RequestParam String processedBy) {
        boolean success = emergencyCallService.processEmergencyCall(id, processedBy);
        if (success) {
            return Result.success("紧急呼叫已开始处理");
        } else {
            return Result.error("紧急呼叫处理失败，可能已被处理或不存在");
        }
    }

    /**
     * 完成紧急呼叫
     *
     * @param id 紧急呼叫ID
     * @return 处理结果
     */
    @PutMapping("/complete/{id}")
    public Result<Void> completeEmergencyCall(@PathVariable Long id) {
        boolean success = emergencyCallService.completeEmergencyCall(id);
        if (success) {
            return Result.success("紧急呼叫已完成处理");
        } else {
            return Result.error("紧急呼叫完成失败，可能状态不正确或不存在");
        }
    }
}
