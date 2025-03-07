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
    private final HealthRecordsMapper healthRecordsMapper;
    // 根据角色ID查询用户信息

    @Override
    public User getInfoWithUser(User user) {
         User user1=userMapper.selectById(user.getId());
         if(user1.getElderId()!=null){
             Elder elder=elderMapper.selectById(user1.getElderId());
             HealthRecords healthRecords=healthRecordsMapper.selectHealthRecordsWithElderInfoByElderId(elder.getId());
             elder.setHealthRecords(healthRecords);
             user1.setElder(elder);
         }else if(user1.getKinId()!=null){
             Kin kin=kinMapper.selectById(user1.getKinId());
             user1.setKin(kin);
         }else if(user1.getStaffId()!=null){
             Staff staff=staffMapper.selectById(user1.getStaffId());
             user1.setStaff(staff);
         }
         return user1;

    }

}