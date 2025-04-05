package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.ElderKinRelation;
import com.communitypension.communitypensionadmin.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
    @Delete("DELETE FROM elder_kin_relation WHERE elder_id = #{elderId} AND kin_id = #{kinId}")
    boolean unbindRelation(@Param("elderId") Long elderId, @Param("kinId") Long kinId, @Param("relationType") String relationType);

    /**
     * 根据老人ID查询所有绑定的家属ID
     * @param elderId 老人ID
     * @return 家属ID列表
     */
    @Select("SELECT kin_id FROM elder_kin_relation WHERE elder_id = #{elderId}")
    List<Long> selectKinIdsByElderId(@Param("elderId") Long elderId);

    /**
     * 根据家属ID查询所有绑定的老人ID
     * @param kinId 家属ID
     * @return 老人ID列表
     */
    @Select("SELECT elder_id FROM elder_kin_relation WHERE kin_id = #{kinId}")
    List<Long> selectElderIdsByKinId(@Param("kinId") Long kinId);

    /**
     * 根据老人ID查询所有绑定的家属信息
     * @param elderId 老人ID
     * @return 家属用户列表
     */
    @Select("SELECT u.* FROM user u JOIN elder_kin_relation r ON u.user_id = r.kin_id WHERE r.elder_id = #{elderId}")
    List<User> selectKinsByElderId(@Param("elderId") Long elderId);

    /**
     * 根据家属ID查询所有绑定的老人信息
     * @param kinId 家属ID
     * @return 老人用户列表
     */
    @Select("SELECT u.* FROM user u JOIN elder_kin_relation r ON u.user_id = r.elder_id WHERE r.kin_id = #{kinId}")
    List<User> selectEldersByKinId(@Param("kinId") Long kinId);

    /**
     * 查询老人和家属的关系类型
     * @param elderId 老人ID
     * @param kinId 家属ID
     * @return 关系类型
     */
    @Select("SELECT relation_type FROM elder_kin_relation WHERE elder_id = #{elderId} AND kin_id = #{kinId}")
    String selectRelationType(@Param("elderId") Long elderId, @Param("kinId") Long kinId);
}