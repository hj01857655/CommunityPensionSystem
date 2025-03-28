package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 老人家属关系实体类
 *
 * @date 2025-03-21
 */
@Data
@TableName("elder_kin_relation")
@KeySequence(value = "elder_kin_relation_seq")
public class ElderKinRelation {
    /**
     * 老人用户ID
     */
    @TableId(value = "elder_id", type = IdType.INPUT)
    private Long elderId;

    /**
     * 家属用户ID
     */
    private Long kinId;

    /**
     * 关系类型(父子/母女等)
     */
    private String relationType;





}
