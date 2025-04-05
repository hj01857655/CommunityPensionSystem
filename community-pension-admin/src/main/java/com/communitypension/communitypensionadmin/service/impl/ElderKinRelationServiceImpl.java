package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.ElderKinRelation;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.mapper.ElderKinRelationMapper;
import com.communitypension.communitypensionadmin.service.ElderKinRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ElderKinRelationServiceImpl extends ServiceImpl<ElderKinRelationMapper, ElderKinRelation> implements ElderKinRelationService {

    @Autowired
    private ElderKinRelationMapper elderKinRelationMapper;

    @Override
    public boolean bindRelation(Long elderId, Long kinId, String relationType) {
        return elderKinRelationMapper.bindRelation(elderId, kinId, relationType);
    }

    @Override
    public boolean unbindRelation(Long elderId, Long kinId, String relationType) {
        return elderKinRelationMapper.unbindRelation(elderId, kinId, relationType);
    }

    @Override
    public List<Long> getKinIdsByElderId(Long elderId) {
        return elderKinRelationMapper.selectKinIdsByElderId(elderId);
    }

    @Override
    public List<Long> getElderIdsByKinId(Long kinId) {
        return elderKinRelationMapper.selectElderIdsByKinId(kinId);
    }

    @Override
    public List<User> getKinsByElderId(Long elderId) {
        return elderKinRelationMapper.selectKinsByElderId(elderId);
    }

    @Override
    public List<User> getEldersByKinId(Long kinId) {
        return elderKinRelationMapper.selectEldersByKinId(kinId);
    }

    @Override
    public String getRelationType(Long elderId, Long kinId) {
        return elderKinRelationMapper.selectRelationType(elderId, kinId);
    }
}