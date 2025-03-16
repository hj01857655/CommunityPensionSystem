package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.*;
import com.communitypension.communitypensionadmin.mapper.*;
import com.communitypension.communitypensionadmin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;
    private final ElderMapper elderMapper;
    private final KinMapper kinMapper;
    private final StaffMapper staffMapper;

    @Override
    public boolean resetPassword(Long userId) {
        boolean  isResetPassword=userMapper.resetPassword(userId);
        if(isResetPassword){
            return true;
        }else{
            logger.info("密码重置失败");
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            Long roleId = user.getRoleId();
            if (roleId == 1) {
                // 查询老人信息
                Elder elder = elderMapper.selectById(user.getElderId());
                user.setElder(elder);
            } else if (roleId == 2) {
                // 查询护理员信息
                Staff staff = staffMapper.selectById(user.getStaffId());
                user.setStaff(staff);
            } else if (roleId == 3) {
                // 查询家属信息
                Kin kin = kinMapper.selectById(user.getKinId());
                user.setKin(kin);
            } else if (roleId == 4) {
                return user;
            }
            return user;
        }
        return null;
    }


}