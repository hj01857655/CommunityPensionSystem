package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.UserMapper;
import com.communitypension.communitypensionadmin.service.RoleService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
    @RequiredArgsConstructor
    @Transactional(rollbackFor = Exception.class)
    public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
        private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
        private final UserMapper userMapper;
        private final RoleService roleService;
        private final JwtUtil jwtUtil;




        @Override
        public Map<String, Object> login(String username, String password, Long roleId) {
            logger.info("User login attempt with username: {} and roleId: {}", username, roleId);

            User user = userMapper.getUserByUsernameAndRole(username, roleId)
                    .orElseThrow(() -> new BusinessException(401, "用户不存在"));

            if(password.equals(user.getPassword())){
                throw new BusinessException(401, "密码错误");
            }

            Role role = roleService.getById(roleId);
            String token = jwtUtil.generateToken(user.getUsername(), role.getRoleName());

            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);
            return data;
        }


        @Override
        public void resetPassword(Long userId, String oldPassword, String newPassword) {
            Optional<User> userOptional = Optional.ofNullable(userMapper.selectById(userId));
            User user = userOptional.orElseThrow(() -> new BusinessException(404, "用户不存在"));


            if(oldPassword.equals(user.getPassword())){
                throw new BusinessException(400, "新密码不能与旧密码相同");
            }
            user.setPassword(newPassword);
            userMapper.updateById(user);
        }




    }