package com.communitypension.communitypensionadmin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Elder;

public interface ElderService extends IService<Elder> {
    void calculateAge(Elder elder);
    void anonymizeIdCard(Elder elder);
}
