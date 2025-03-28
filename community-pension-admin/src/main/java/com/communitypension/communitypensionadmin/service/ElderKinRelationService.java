package com.communitypension.communitypensionadmin.service;

public interface ElderKinRelationService {
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
     * @return 是否解绑成功
     */
    boolean unbindRelation(Long elderId, Long kinId, String relationType);
}