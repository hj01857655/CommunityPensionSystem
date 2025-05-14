package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.ElderKinRelation;
import com.communitypension.communitypensionadmin.entity.User;

import java.util.List;

public interface ElderKinRelationService extends IService<ElderKinRelation> {
    /**
     * 绑定老人和家属关系
     * @param elderId 老人ID
     * @param kinId 家属ID
     * @param relationType 关系类型
     * @return 是否绑定成功
     */
    boolean bindRelation(Long elderId, Long kinId, String relationType);

    /**
     * 解绑老人和家属关系
     * @param elderId 老人ID
     * @param kinId 家属ID
     * @param relationType 关系类型
     * @return 是否解绑成功
     */
    boolean unbindRelation(Long elderId, Long kinId, String relationType);

    /**
     * 根据老人ID查询所有绑定的家属ID
     * @param elderId 老人ID
     * @return 家属ID列表
     */
    List<Long> getKinIdsByElderId(Long elderId);

    /**
     * 根据家属ID查询所有绑定的老人ID
     * @param kinId 家属ID
     * @return 老人ID列表
     */
    List<Long> getElderIdsByKinId(Long kinId);

    /**
     * 根据老人ID查询所有绑定的家属信息
     * @param elderId 老人ID
     * @return 家属用户列表
     */
    List<User> getKinsByElderId(Long elderId);

    /**
     * 根据家属ID查询所有绑定的老人信息
     * @param kinId 家属ID
     * @return 老人用户列表
     */
    List<User> getEldersByKinId(Long kinId);

    /**
     * 查询老人和家属的关系类型
     * @param elderId 老人ID
     * @param kinId 家属ID
     * @return 关系类型
     */
    String getRelationType(Long elderId, Long kinId);
}