package com.communitypension.communitypensionadmin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Staff;
import com.communitypension.communitypensionadmin.mapper.StaffMapper;
import com.communitypension.communitypensionadmin.service.StaffService;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {
}
