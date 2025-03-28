package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.ElderKinRelation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ElderKinRelationMapper extends BaseMapper<ElderKinRelation> {
    /**
     * 绑定老人和家属关系
     * @param elderId 老人ID
     * @param kinId 家属ID
     * @param relationType 关系类型
     */
    @Insert("INSERT INTO elder_kin_relation(elder_id, kin_id, relation_type) VALUES(#{elderId}, #{kinId}, #{relationType})")
    boolean bindRelation(@Param("elderId") Long elderId, @Param("kinId") Long kinId, @Param("relationType") String relationType);

    /**
     * 解绑老人和家属关系
     * @param elderId 老人ID
     * @param kinId 家属ID
     */
    @Insert("DELETE FROM elder_kin_relation WHERE elder_id = #{elderId} AND kin_id = #{kinId}")
    boolean unbindRelation(@Param("elderId") Long elderId, @Param("kinId") Long kinId, @Param("relationType") String relationType);
}