package com.communitypension.communitypensionadmin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Kin;
import com.communitypension.communitypensionadmin.mapper.KinMapper;
import com.communitypension.communitypensionadmin.service.KinService;
import org.springframework.stereotype.Service;

@Service
public class KinServiceImpl extends ServiceImpl<KinMapper, Kin> implements KinService {
}
