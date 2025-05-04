package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.dto.*;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.enums.RoleEnum;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.vo.ElderKinVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询参数
     * @return 用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询用户列表")
    public Result<Page<UserDTO>> getUserList(UserQueryDTO queryDTO) {
        try {
            // 参数校验
            if (queryDTO.getCurrent() == null || queryDTO.getCurrent() < 1) {
                queryDTO.setCurrent(1);
            }
            if (queryDTO.getSize() == null || queryDTO.getSize() < 1 || queryDTO.getSize() > 100) {
                queryDTO.setSize(10);
            }

            // 构建查询条件
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            if (StringUtils.hasText(queryDTO.getUsername())) {
                queryWrapper.like("username", queryDTO.getUsername());
            }
            if (queryDTO.getIsActive() != null) {
                queryWrapper.eq("is_active", queryDTO.getIsActive());
            }
            if (StringUtils.hasText(queryDTO.getStartTime())) {
                queryWrapper.ge("create_time", queryDTO.getStartTime());
            }
            if (StringUtils.hasText(queryDTO.getEndTime())) {
                queryWrapper.le("create_time", queryDTO.getEndTime());
            }

            // 默认按创建时间倒序
            queryWrapper.orderByDesc("create_time");

            // 执行查询
            // 获取原始用户分页数据
            Page<User> userPage = userService.getUserPage(new Page<>(queryDTO.getCurrent(), queryDTO.getSize()), queryWrapper);
            // 转换为DTO分页
            Page<UserDTO> dtoPage = new Page<>();
            BeanUtils.copyProperties(userPage, dtoPage, "records");
            dtoPage.setRecords(userPage.getRecords().stream().map(user -> {
                UserDTO dto = new UserDTO();
                // 复制基础属性
                BeanUtils.copyProperties(user, dto);
                // 设置角色信息（需要确保user对象携带了角色数据）
                dto.setRoleIds(userService.getUserRoleIds(user.getUserId()));
                dto.setRoleNames(userService.getUserRoleNames(user.getUserId()));
                dto.setRoles(userService.getUserRoles(user.getUserId()));
                return dto;
            }).collect(Collectors.toList()));
            return Result.success("查询成功", dtoPage);
        } catch (Exception e) {
            logger.error("查询用户列表失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "根据用户名查询用户信息")
    public Result<UserDTO> getUserInfoByUserId(@PathVariable Long userId) {
        try {
            // 参数校验
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }
            // 执行查询
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            // 转换为DTO
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);
            // 设置角色信息
            dto.setRoleIds(userService.getUserRoleIds(user.getUserId()));
            dto.setRoleNames(userService.getUserRoleNames(user.getUserId()));
            dto.setRoles(userService.getUserRoles(user.getUserId()));

            // 设置绑定关系
            if (dto.getRoleIds().contains(RoleEnum.ELDER.getId())) {
                // 如果是老人，获取绑定的家属列表
                dto.setBindKinIds(userService.getKinIdsByElderId(userId));
            } else if (dto.getRoleIds().contains(RoleEnum.KIN.getId())) {
                // 如果是家属，获取绑定的老人列表
                dto.setBindElderIds(userService.getElderIdsByKinId(userId));
            }

            // 设置其他必要字段
            dto.setBirthday(user.getBirthday());
            dto.setIdCard(user.getIdCard());
            dto.setEmergencyContactName(user.getEmergencyContactName());
            dto.setEmergencyContactPhone(user.getEmergencyContactPhone());
            dto.setHealthCondition(user.getHealthCondition());
            dto.setRemark(user.getRemark());
            
            return Result.success("查询成功", dto);
        } catch (Exception e) {
            logger.error("获取用户信息失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详细信息（包含绑定关系）
     */
    @GetMapping("/{userId}/detail")
    @Operation(summary = "获取用户详细信息")
    public Result<UserDTO> getUserDetail(@PathVariable Long userId) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }
            // 获取基本信息
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);

            // 设置角色信息
            dto.setRoleIds(userService.getUserRoleIds(user.getUserId()));
            dto.setRoleNames(userService.getUserRoleNames(user.getUserId()));
            dto.setRoles(userService.getUserRoles(user.getUserId()));

            // 设置绑定关系
            if (dto.getRoleIds().contains(RoleEnum.ELDER.getId())) {
                // 如果是老人，获取绑定的家属列表
                dto.setBindKinIds(userService.getKinIdsByElderId(userId));
            } else if (dto.getRoleIds().contains(RoleEnum.KIN.getId())) {
                // 如果是家属，获取绑定的老人列表
                dto.setBindElderIds(userService.getElderIdsByKinId(userId));
            }

            return Result.success("查询成功", dto);
        } catch (Exception e) {
            logger.error("获取用户详情失败: {}", e.getMessage());
            return Result.error("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增用户（支持同时绑定关系）
     */
    @PostMapping("/add")
    @Operation(summary = "新增用户")
    public Result<Void> addUser(@RequestBody @Validated UserDTO userDTO) {
        try {
            // 基本验证
            if (!StringUtils.hasText(userDTO.getUsername())) {
                return Result.error("用户名不能为空");
            }
            if (!StringUtils.hasText(userDTO.getPassword())) {
                return Result.error("密码不能为空");
            }
            if (userDTO.getRoleIds() == null || userDTO.getRoleIds().isEmpty()) {
                return Result.error("必须指定至少一个角色");
            }

            // 验证绑定关系
            if (userDTO.getRoleIds().contains(RoleEnum.KIN.getId())) {
                // 如果是家属，验证绑定的老人
                if (userDTO.getBindElderIds() != null && !userDTO.getBindElderIds().isEmpty()
                    && !StringUtils.hasText(userDTO.getRelationType())) {
                    return Result.error("绑定老人时必须指定关系类型");
                }
            } else if (userDTO.getRoleIds().contains(RoleEnum.ELDER.getId())) {
                // 如果是老人，验证绑定的家属
                if (userDTO.getBindKinIds() != null && !userDTO.getBindKinIds().isEmpty()
                    && !StringUtils.hasText(userDTO.getRelationType())) {
                    return Result.error("绑定家属时必须指定关系类型");
                }
            }

            userService.addUserWithRoles(userDTO);
            return Result.success("新增用户成功");
        } catch (Exception e) {
            logger.error("新增用户失败: {}", e.getMessage());
            return Result.error("新增用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户（支持更新绑定关系）
     */
    @PutMapping("/{userId}")
    @Operation(summary = "更新用户")
    public Result<Void> updateUser(@PathVariable Long userId, @RequestBody @Validated UserDTO userDTO) {
        try {
            if (userDTO == null) {
                return Result.error("用户信息不能为空");
            }

            // 确保userId一致
            userDTO.setUserId(userId);

            // 确保roleIds不为null
            if (userDTO.getRoleIds() == null) {
                userDTO.setRoleIds(new ArrayList<>());
            }

            // 验证角色组合
            userService.validateRoleCombination(userDTO.getRoleIds());

            // 更新用户信息
            userService.updateUser(userDTO);

            return Result.success("更新用户成功");
        } catch (Exception e) {
            logger.error("更新用户失败: {}", e.getMessage());
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }
            userService.deleteUser(userId);
            return Result.success("删除用户成功");
        } catch (Exception e) {
            logger.error("删除用户失败: {}", e.getMessage());
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户")
    public Result<Void> deleteUsers(@RequestBody List<Long> userIds) {
        try {
            if (userIds == null || userIds.isEmpty()) {
                return Result.error("用户ID列表不能为空");
            }
            userService.deleteUsers(userIds);
            return Result.success("批量删除用户成功");
        } catch (Exception e) {
            logger.error("批量删除用户失败: {}", e.getMessage());
            return Result.error("批量删除用户失败: " + e.getMessage());
        }
    }
    /**
     * 重置用户密码
     */
    @PutMapping("/{userId}/reset-password")
    @Operation(summary = "重置用户密码")
    public Result<Void> resetPassword(@PathVariable Long userId) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }
            userService.resetPassword(userId);
            return Result.success("重置密码成功");
        } catch (Exception e) {
            logger.error("重置密码失败: {}", e.getMessage());
            return Result.error("重置密码失败: " + e.getMessage());
        }
    }
    /**
     * 修改密码
     * @param userId 用户ID
     * @param passwordDTO 密码信息
     */
    @PutMapping("/{userId}/password")
    @Operation(summary = "修改用户密码")
    public Result<Void> updatePassword(@PathVariable Long userId, @RequestBody @Validated PasswordDTO passwordDTO) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }
            userService.updatePassword(userId, passwordDTO);
            return Result.success("修改密码成功");
        } catch (Exception e) {
            logger.error("修改密码失败: {}", e.getMessage());
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }




    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传用户头像")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            String avatarUrl = userService.uploadAvatar(file);
            return Result.success("上传头像成功", avatarUrl);
        } catch (Exception e) {
            logger.error("上传头像失败: {}", e.getMessage());
            return Result.error("上传头像失败: " + e.getMessage());
        }
    }



    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    @Operation(summary = "更新用户状态")
    public Result<Void> updateStatus(@PathVariable Long userId, @RequestBody StatusDTO statusDTO) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }
            if (statusDTO.getIsActive()==0) {
                logger.warn("禁用用户: {}", userId);
            }
            userService.updateStatus(userId, statusDTO.getIsActive());
            return Result.success("更新状态成功");
        } catch (Exception e) {
            logger.error("更新状态失败: {}", e.getMessage());
            return Result.error("更新状态失败: " + e.getMessage());
        }
    }
    /**
     * 获取未绑定家属的老人列表
     */
    @GetMapping("/unbound/elders")
    @Operation(summary = "获取未绑定家属的老人列表")
    public Result<List<UserDTO>> getUnboundElders() {
        try {
            List<User> users = userService.getUnboundElders();
            List<UserDTO> elders = users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    BeanUtils.copyProperties(user, dto);
                    dto.setRoleIds(userService.getUserRoleIds(user.getUserId()));
                    dto.setRoleNames(userService.getUserRoleNames(user.getUserId()));
                    return dto;
                })
                .collect(Collectors.toList());
            return Result.success("查询成功", elders);
        } catch (Exception e) {
            logger.error("获取未绑定家属老人列表失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取未绑定老人的家属列表
     */
    @GetMapping("/unbound/kins")
    @Operation(summary = "获取未绑定老人的家属列表")
    public Result<List<UserDTO>> getUnboundKins() {
        try {
            List<User> users = userService.getUnboundKins();
            List<UserDTO> kins = users.stream()
                    .map(user -> {
                        UserDTO dto = new UserDTO();
                        BeanUtils.copyProperties(user, dto);
                        dto.setRoleIds(userService.getUserRoleIds(user.getUserId()));
                        dto.setRoleNames(userService.getUserRoleNames(user.getUserId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
            return Result.success("查询成功", kins);
        } catch (Exception e) {
            logger.error("获取未绑定老人的家属列表失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    @Operation(summary = "绑定老人和家属关系")
    @PostMapping("/bind-relation")
    public Result<Boolean> bindElderKinRelation(
            @Parameter(description = "老人ID") @RequestParam Long elderId,
            @Parameter(description = "家属ID") @RequestParam Long kinId,
            @Parameter(description = "关系类型") @RequestParam String relationType) {
        // 检查老人是否已有绑定家属
        List<Long> existingKins = userService.getKinIdsByElderId(elderId);
        if (!existingKins.isEmpty()) {
            return Result.error("该老人已绑定家属，一个老人只能绑定一个家属");
        }
        boolean success = userService.bindElderKinRelation(elderId, kinId, relationType);
        return Result.success(success);
    }

    @Operation(summary = "解绑老人和家属关系")
    @PostMapping("/unbind-relation")
    public Result<Boolean> unbindElderKinRelation(
            @Parameter(description = "老人ID") @RequestParam Long elderId,
            @Parameter(description = "家属ID") @RequestParam Long kinId,
            @Parameter(description = "关系类型") @RequestParam String relationType) {
        boolean success = userService.unbindElderKinRelation(elderId, kinId, relationType);
        return Result.success(success);
    }


    /**
     * 获取老人的家属列表
     */
    @GetMapping("/kins/{elderId}")
    @Operation(summary = "获取老人的家属列表")
    public Result<List<UserSimpleDTO>> getKinsByElderId(@PathVariable Long elderId) {
        try {
            // 验证老人是否存在
            User elder = userService.getById(elderId);
            if (elder == null) {
                return Result.error("老人不存在");
            }

            List<User> kins = userService.getKinsByElderId(elderId);
            List<UserSimpleDTO> kinDTOs = new ArrayList<>();

            for (User kin : kins) {
                UserSimpleDTO dto = new UserSimpleDTO();
                dto.setUserId(kin.getUserId());
                dto.setUsername(kin.getUsername());
                dto.setName(kin.getName());
                // 获取关系类型
                String relationType = userService.getRelationType(elderId, kin.getUserId());
                dto.setRelationType(relationType);
                kinDTOs.add(dto);
            }

            return Result.success("查询成功", kinDTOs);
        } catch (Exception e) {
            logger.error("获取老人的家属列表失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取家属的老人列表
     */
    @GetMapping("/elders/{kinId}")
    @Operation(summary = "获取家属的老人列表")
    public Result<List<UserSimpleDTO>> getEldersByKinId(@PathVariable Long kinId) {
        try {
            // 验证家属是否存在
            User kin = userService.getById(kinId);
            if (kin == null) {
                return Result.error("家属不存在");
            }

            List<User> elders = userService.getEldersByKinId(kinId);
            List<UserSimpleDTO> elderDTOs = new ArrayList<>();

            for (User elder : elders) {
                UserSimpleDTO dto = new UserSimpleDTO();
                dto.setUserId(elder.getUserId());
                dto.setUsername(elder.getUsername());
                dto.setName(elder.getName());
                // 获取关系类型
                String relationType = userService.getRelationType(elder.getUserId(), kinId);
                dto.setRelationType(relationType);
                elderDTOs.add(dto);
            }

            return Result.success("查询成功", elderDTOs);
        } catch (Exception e) {
            logger.error("获取家属的老人列表失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取老人和家属的关系详情
     */
    @GetMapping("/relation-detail")
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
            String relationType = userService.getRelationType(elderId, kinId);
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
            logger.error("获取老人和家属的关系详情失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/{userId}/roles")
    @Operation(summary = "获取用户角色列表")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }
            List<Long> roleIds = userService.getUserRoleIds(userId);
            return Result.success("查询成功", roleIds);
        } catch (Exception e) {
            logger.error("获取用户角色失败: {}", e.getMessage());
            return Result.error("获取用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有老人列表
     */
    @GetMapping("/elders")
    @Operation(summary = "获取所有老人列表")
    public Result<List<UserDTO>> getAllElders() {
        try {
            List<User> users = userService.getAllElders();
            List<UserDTO> elders = users.stream()
                    .map(user -> {
                        UserDTO dto = new UserDTO();
                        BeanUtils.copyProperties(user, dto);

                        dto.setRoleIds(userService.getUserRoleIds(user.getUserId()));
                        dto.setRoles(userService.getUserRoles(user.getUserId()));
                        dto.setRoleNames(userService.getUserRoleNames(user.getUserId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
            return Result.success("查询成功", elders);
        } catch (Exception e) {
            logger.error("获取老人列表失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

}

