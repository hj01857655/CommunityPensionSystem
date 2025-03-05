package com.communitypension.communitypensionadmin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Elder;
import com.communitypension.communitypensionadmin.mapper.ElderMapper;
import com.communitypension.communitypensionadmin.service.ElderService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper, Elder> implements ElderService {
    @Override
    public void calculateAge(Elder elder) {
        if (elder.getBirthday() != null) {
            LocalDate birthDate = elder.getBirthday();
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(birthDate, currentDate);
            elder.setAge(period.getYears());
            updateById(elder);
        }
    }

    @Override
    public void anonymizeIdCard(Elder elder) {
        if (elder != null && elder.getIdCard() != null) {
            String idCard = elder.getIdCard();
            elder.setIdCard(idCard.substring(0, 4) + "********" + idCard.substring(idCard.length() - 4));
        }
    }
}
