package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.converter.UserConverter;
import com.communitypension.communitypensionadmin.dto.ElderDTO;
import com.communitypension.communitypensionadmin.dto.KinDTO;
import com.communitypension.communitypensionadmin.dto.StaffDTO;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.enums.RoleEnum;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.vo.ElderUserVO;
import com.communitypension.communitypensionadmin.vo.KinUserVO;
import com.communitypension.communitypensionadmin.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色控制器
 * 演示如何根据角色使用不同的DTO和VO
 */
@RestController
@RequestMapping("/api/user-role")
@Tag(name = "用户角色管理", description = "用户角色相关接口")
public class UserRoleController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    /**
     * 创建老人用户
     */
    @PostMapping("/elder")
    @Operation(summary = "创建老人用户")
    public Result<?> createElderUser(@RequestBody @Valid ElderDTO elderDTO) {
        // 设置角色ID
        elderDTO.setRoleIds(List.of(RoleEnum.ELDER.getId()));

        // 调用服务层保存用户
        userService.addUserWithRoles(elderDTO);

        return Result.success("创建老人用户成功");
    }

    /**
     * 创建家属用户
     */
    @PostMapping("/kin")
    @Operation(summary = "创建家属用户")
    public Result<?> createKinUser(@RequestBody @Valid KinDTO kinDTO) {
        // 设置角色ID
        kinDTO.setRoleIds(List.of(RoleEnum.KIN.getId()));

        // 调用服务层保存用户
        userService.addUserWithRoles(kinDTO);

        return Result.success("创建家属用户成功");
    }

    /**
     * 创建社区工作人员用户
     */
    @PostMapping("/staff")
    @Operation(summary = "创建社区工作人员用户")
    public Result<?> createStaffUser(@RequestBody @Valid StaffDTO staffDTO) {
        // 设置角色ID
        staffDTO.setRoleIds(List.of(RoleEnum.STAFF.getId()));

        // 调用服务层保存用户
        userService.addUserWithRoles(staffDTO);

        return Result.success("创建社区工作人员用户成功");
    }

    /**
     * 获取用户详情（根据角色返回不同VO）
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情")
    public Result<?> getUserById(@Parameter(description = "用户ID") @PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 获取用户角色
        List<Long> roleIds = userService.getUserRoleIds(userId);

        // 根据角色返回不同的VO
        if (roleIds.contains(RoleEnum.ELDER.getId())) { // 老人角色
            ElderUserVO elderUserVO = userConverter.toElderUserVO(user);
            // 加载老人特有信息
            // ...
            return Result.success(elderUserVO);
        } else if (roleIds.contains(RoleEnum.KIN.getId())) { // 家属角色
            KinUserVO kinUserVO = userConverter.toKinUserVO(user);
            // 加载家属特有信息
            // ...
            return Result.success(kinUserVO);
        } else {
            // 其他角色返回基础UserVO
            UserVO userVO = userConverter.toUserVO(user);
            return Result.success(userVO);
        }
    }
}
