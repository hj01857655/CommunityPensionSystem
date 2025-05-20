package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.pojo.dto.UserDTO;
import com.communitypension.communitypensionadmin.pojo.dto.UserQueryDTO;
import com.communitypension.communitypensionadmin.pojo.dto.UserSimpleDTO;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.enums.RoleEnum;
import com.communitypension.communitypensionadmin.mapper.RoleMapper;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.pojo.vo.ElderKinVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {
    private final UserService userService;
    private final RoleMapper roleMapper;

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
                dto.setRoleId(userService.getUserRoleId(user.getUserId()));
                dto.setRoleName(userService.getUserRoleName(user.getUserId()));
                dto.setRole(userService.getUserRole(user.getUserId()));
                return dto;
            }).collect(Collectors.toList()));
            return Result.success("查询成功", dtoPage);
        } catch (Exception e) {
            log.error("查询用户列表失败: {}", e.getMessage());
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
            dto.setRoleId(userService.getUserRoleId(user.getUserId()));
            dto.setRoleName(userService.getUserRoleName(user.getUserId()));
            dto.setRole(userService.getUserRole(user.getUserId()));

            // 设置绑定关系
            if (dto.getRoleId() != null && dto.getRoleId().equals(RoleEnum.ELDER.getId())) {
                // 如果是老人，获取绑定的家属ID列表
                List<Long> kinIds = userService.getKinIdsByElderId(userId);
                if (kinIds != null && !kinIds.isEmpty()) {
                    dto.setBindKinIds(kinIds); // 设置所有绑定的家属ID
                }
            } else if (dto.getRoleId() != null && dto.getRoleId().equals(RoleEnum.KIN.getId())) {
                // 如果是家属，获取绑定的老人ID列表
                List<Long> elderIds = userService.getElderIdsByKinId(userId);
                if (elderIds != null && !elderIds.isEmpty()) {
                    dto.setBindElderIds(elderIds); // 设置所有绑定的老人ID
                }
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
            log.error("获取用户信息失败: {}", e.getMessage());
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
            dto.setRoleId(userService.getUserRoleId(userId));
            dto.setRoleName(userService.getUserRoleName(userId));
            dto.setRole(userService.getUserRole(userId));

            // 设置绑定关系
            if (dto.getRoleId() != null && dto.getRoleId().equals(RoleEnum.ELDER.getId())) {
                // 如果是老人，获取绑定的家属ID列表
                List<Long> kinIds = userService.getKinIdsByElderId(userId);
                if (kinIds != null && !kinIds.isEmpty()) {
                    dto.setBindKinIds(kinIds); // 设置所有绑定的家属ID列表
                }
            } else if (dto.getRoleId() != null && dto.getRoleId().equals(RoleEnum.KIN.getId())) {
                // 如果是家属，获取绑定的老人ID列表
                List<Long> elderIds = userService.getElderIdsByKinId(userId);
                if (elderIds != null && !elderIds.isEmpty()) {
                    dto.setBindElderIds(elderIds); // 设置所有绑定的老人ID列表
                }
            }

            return Result.success("查询成功", dto);
        } catch (Exception e) {
            log.error("获取用户详情失败: {}", e.getMessage());
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
            if (userDTO.getRoleId() == null) {
                return Result.error("必须指定角色");
            }

            // 验证绑定关系
            if (userDTO.getRoleId().equals(RoleEnum.KIN.getId())) {
                // 如果是家属，验证绑定的老人
                if (userDTO.getBindElderIds() != null && !userDTO.getBindElderIds().isEmpty() && !StringUtils.hasText(userDTO.getRelationType())) {
                    return Result.error("绑定老人时必须指定关系类型");
                }
            } else if (userDTO.getRoleId().equals(RoleEnum.ELDER.getId())) {
                // 如果是老人，验证绑定的家属
                if (userDTO.getBindKinIds() != null && !userDTO.getBindKinIds().isEmpty() && !StringUtils.hasText(userDTO.getRelationType())) {
                    return Result.error("绑定家属时必须指定关系类型");
                }
            }

            userService.addUserWithRole(userDTO);
            return Result.success("新增用户成功");
        } catch (Exception e) {
            log.error("新增用户失败: {}", e.getMessage());
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

            // 确保roleId不为null
            if (userDTO.getRoleId() == null) {
                return Result.error("必须指定角色");
            }

            // 验证角色
            userService.validateRole(userDTO.getRoleId());

            // 验证绑定关系
            if (userDTO.getRoleId().equals(RoleEnum.KIN.getId())) {
                // 如果是家属，验证绑定的老人
                if (userDTO.getBindElderIds() != null && !userDTO.getBindElderIds().isEmpty() && !StringUtils.hasText(userDTO.getRelationType())) {
                    return Result.error("绑定老人时必须指定关系类型");
                }
            } else if (userDTO.getRoleId().equals(RoleEnum.ELDER.getId())) {
                // 如果是老人，验证绑定的家属
                if (userDTO.getBindKinIds() != null && !userDTO.getBindKinIds().isEmpty() && !StringUtils.hasText(userDTO.getRelationType())) {
                    return Result.error("绑定家属时必须指定关系类型");
                }
            }

            // 更新用户信息
            userService.updateUser(userDTO);

            return Result.success("更新用户成功");
        } catch (Exception e) {
            log.error("更新用户失败: {}", e.getMessage());
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户角色
     */
    @GetMapping("/{userId}/role")
    @Operation(summary = "获取用户角色")
    public Result<Long> getUserRole(@PathVariable Long userId) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }
            Long roleId = userService.getUserRoleId(userId);
            return Result.success("查询成功", roleId);
        } catch (Exception e) {
            log.error("获取用户角色失败: {}", e.getMessage());
            return Result.error("获取用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户角色授权信息
     */
    @GetMapping("/{userId}/auth-role")
    @Operation(summary = "获取用户角色授权信息")
    public Result<Map<String, Object>> getAuthRole(@PathVariable Long userId) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }

            // 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 获取所有角色
            List<Role> allRoles = roleMapper.selectList(null);

            // 获取用户已分配的角色ID（单角色风格，返回Long）
            Long userRoleId = userService.getUserRoleId(userId);

            // 标记用户已分配的角色
            List<Map<String, Object>> roleList = allRoles.stream().map(role -> {
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("roleId", role.getRoleId());
                roleMap.put("roleName", role.getRoleName());
                roleMap.put("roleKey", role.getRoleKey());
                roleMap.put("createTime", role.getCreateTime());
                // 标记是否已分配
                roleMap.put("flag", userRoleId != null && userRoleId.equals(role.getRoleId()));
                return roleMap;
            }).collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("roleList", roleList);

            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取用户角色授权信息失败: {}", e.getMessage());
            return Result.error("获取用户角色授权信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户角色授权信息
     */
    @PutMapping("/{userId}/auth-role")
    @Operation(summary = "更新用户角色授权信息")
    public Result<Void> updateAuthRole(@PathVariable Long userId, @RequestBody Map<String, String> data) {
        try {
            if (userId <= 0) {
                return Result.error("用户ID无效");
            }

            // 获取角色ID
            String roleIdStr = data.get("roleId");
            if (!StringUtils.hasText(roleIdStr)) {
                return Result.error("角色ID不能为空");
            }

            // 将角色ID字符串转换为Long
            Long roleId;
            try {
                roleId = Long.parseLong(roleIdStr);
            } catch (NumberFormatException e) {
                return Result.error("角色ID格式不正确");
            }

            // 分配角色
            userService.assignRole(userId, roleId);

            return Result.success("授权成功");
        } catch (Exception e) {
            log.error("更新用户角色授权信息失败: {}", e.getMessage());
            return Result.error("更新用户角色授权信息失败: " + e.getMessage());
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

                        dto.setRoleId(userService.getUserRoleId(user.getUserId()));
                        dto.setRoleName(userService.getUserRoleName(user.getUserId()));
                        dto.setRole(userService.getUserRole(user.getUserId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
            return Result.success("查询成功", elders);
        } catch (Exception e) {
            log.error("获取老人列表失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
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
                        dto.setRoleId(userService.getUserRoleId(user.getUserId()));
                        dto.setRoleName(userService.getUserRoleName(user.getUserId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
            return Result.success("查询成功", elders);
        } catch (Exception e) {
            log.error("获取未绑定家属老人列表失败: {}", e.getMessage());
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
                        dto.setRoleId(userService.getUserRoleId(user.getUserId()));
                        dto.setRoleName(userService.getUserRoleName(user.getUserId()));
                        return dto;
                    })
                    .collect(Collectors.toList());
            return Result.success("查询成功", kins);
        } catch (Exception e) {
            log.error("获取未绑定老人的家属列表失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "绑定老人和家属关系")
    @PostMapping("/bind-relation")
    public Result<Boolean> bindElderKinRelation(
            @Parameter(description = "老人ID") @RequestParam Long elderId,
            @Parameter(description = "家属ID") @RequestParam Long kinId,
            @Parameter(description = "关系类型") @RequestParam String relationType) {
        // 移除对老人已绑定家属的检查，允许多对多关系
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
            log.error("获取老人的家属列表失败: {}", e.getMessage());
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
            log.error("获取家属的老人列表失败: {}", e.getMessage());
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
            log.error("获取老人和家属的关系详情失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    /**
     * 删除用户（RESTful 风格）
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return Result.error("用户ID无效");
            }
            userService.deleteUser(userId);
            return Result.success("删除用户成功");
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage());
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

}

