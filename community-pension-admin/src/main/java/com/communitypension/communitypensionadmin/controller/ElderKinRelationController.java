package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.dto.ElderKinDTO;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.enums.RoleEnum;
import com.communitypension.communitypensionadmin.service.ElderKinRelationService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.vo.ElderKinVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 老人家属关系管理控制器
 */
@RestController
@RequestMapping("/api/elder-kin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "老人家属关系管理")
public class ElderKinRelationController {

    private final ElderKinRelationService elderKinRelationService;
    private final UserService userService;

    /**
     * 绑定老人和家属关系
     */
    @PostMapping("/bind")
    @Operation(summary = "绑定老人和家属关系")
    public Result<Boolean> bindRelation(@RequestBody @Validated ElderKinDTO elderKinDTO) {
        try {
            // 验证用户角色
            User elder = userService.getById(elderKinDTO.getElderId());
            User kin = userService.getById(elderKinDTO.getKinId());
            
            if (elder == null || kin == null) {
                return Result.error("用户不存在");
            }
            
            // 验证角色
            if (!userService.hasRole(elder.getUserId(), RoleEnum.ELDER.getId())) {
                return Result.error("第一个用户必须是老人角色");
            }
            
            if (!userService.hasRole(kin.getUserId(), RoleEnum.KIN.getId())) {
                return Result.error("第二个用户必须是家属角色");
            }
            
            // 检查老人是否已有绑定家属
            List<Long> existingKins = elderKinRelationService.getKinIdsByElderId(elderKinDTO.getElderId());
            if (!existingKins.isEmpty()) {
                return Result.error("该老人已绑定家属，请先解绑后再绑定新家属");
            }
            
            boolean success = elderKinRelationService.bindRelation(
                    elderKinDTO.getElderId(), 
                    elderKinDTO.getKinId(), 
                    elderKinDTO.getRelationType());
            
            return Result.success("绑定成功", success);
        } catch (Exception e) {
            log.error("绑定老人和家属关系失败", e);
            return Result.error("绑定失败: " + e.getMessage());
        }
    }

    /**
     * 解绑老人和家属关系
     */
    @DeleteMapping("/unbind")
    @Operation(summary = "解绑老人和家属关系")
    public Result<Boolean> unbindRelation(
            @Parameter(description = "老人ID") @RequestParam Long elderId,
            @Parameter(description = "家属ID") @RequestParam Long kinId) {
        try {
            // 验证关系是否存在
            String relationType = elderKinRelationService.getRelationType(elderId, kinId);
            if (relationType == null) {
                return Result.error("该老人和家属未绑定关系");
            }
            
            boolean success = elderKinRelationService.unbindRelation(elderId, kinId, relationType);
            return Result.success("解绑成功", success);
        } catch (Exception e) {
            log.error("解绑老人和家属关系失败", e);
            return Result.error("解绑失败: " + e.getMessage());
        }
    }

    /**
     * 获取老人的家属列表
     */
    @GetMapping("/kins/{elderId}")
    @Operation(summary = "获取老人的家属列表")
    public Result<List<User>> getKinsByElderId(@PathVariable Long elderId) {
        try {
            // 验证老人是否存在
            User elder = userService.getById(elderId);
            if (elder == null) {
                return Result.error("老人不存在");
            }
            
            List<User> kins = elderKinRelationService.getKinsByElderId(elderId);
            return Result.success("查询成功", kins);
        } catch (Exception e) {
            log.error("获取老人的家属列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取家属的老人列表
     */
    @GetMapping("/elders/{kinId}")
    @Operation(summary = "获取家属的老人列表")
    public Result<List<User>> getEldersByKinId(@PathVariable Long kinId) {
        try {
            // 验证家属是否存在
            User kin = userService.getById(kinId);
            if (kin == null) {
                return Result.error("家属不存在");
            }
            
            List<User> elders = elderKinRelationService.getEldersByKinId(kinId);
            return Result.success("查询成功", elders);
        } catch (Exception e) {
            log.error("获取家属的老人列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取老人和家属的关系详情
     */
    @GetMapping("/relation")
    @Operation(summary = "获取老人和家属的关系详情")
    public Result<ElderKinVO> getRelationDetail(
            @Parameter(description = "老人ID") @RequestParam Long elderId,
            @Parameter(description = "家属ID") @RequestParam Long kinId) {
        try {
            // 验证用户是否存在
            User elder = userService.getById(elderId);
            User kin = userService.getById(kinId);
            
            if (elder == null || kin == null) {
                return Result.error("用户不存在");
            }
            
            // 验证关系是否存在
            String relationType = elderKinRelationService.getRelationType(elderId, kinId);
            if (relationType == null) {
                return Result.error("该老人和家属未绑定关系");
            }
            
            // 构建关系详情
            ElderKinVO vo = new ElderKinVO();
            
            // 设置老人信息
            vo.setElderId(elder.getUserId());
            vo.setElderUsername(elder.getUsername());
            vo.setElderName(elder.getName());
            vo.setElderGender(elder.getGender());
            vo.setElderPhone(elder.getPhone());
            vo.setElderIdCard(elder.getIdCard());
            vo.setElderBirthday(elder.getBirthday());
            vo.setElderAge(elder.getAge());
            vo.setElderAddress(elder.getAddress());
            vo.setElderHealthCondition(elder.getHealthCondition());
            vo.setElderEmergencyContactName(elder.getEmergencyContactName());
            vo.setElderEmergencyContactPhone(elder.getEmergencyContactPhone());
            
            // 设置家属信息
            vo.setKinId(kin.getUserId());
            vo.setKinUsername(kin.getUsername());
            vo.setKinName(kin.getName());
            vo.setKinGender(kin.getGender());
            vo.setKinPhone(kin.getPhone());
            vo.setKinEmail(kin.getEmail());
            vo.setKinAddress(kin.getAddress());
            
            // 设置关系信息
            vo.setRelationType(relationType);
            
            return Result.success("查询成功", vo);
        } catch (Exception e) {
            log.error("获取老人和家属的关系详情失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取未绑定家属的老人列表
     */
    @GetMapping("/unbound-elders")
    @Operation(summary = "获取未绑定家属的老人列表")
    public Result<List<User>> getUnboundElders() {
        try {
            List<User> elders = userService.getUnboundElders();
            return Result.success("查询成功", elders);
        } catch (Exception e) {
            log.error("获取未绑定家属的老人列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取未绑定老人的家属列表
     */
    @GetMapping("/unbound-kins")
    @Operation(summary = "获取未绑定老人的家属列表")
    public Result<List<User>> getUnboundKins() {
        try {
            List<User> kins = userService.getUnboundKins();
            return Result.success("查询成功", kins);
        } catch (Exception e) {
            log.error("获取未绑定老人的家属列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
