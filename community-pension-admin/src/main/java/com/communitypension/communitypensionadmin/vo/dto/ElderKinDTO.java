package com.communitypension.communitypensionadmin.vo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.transaction.Transactional;
import lombok.Data;
/**
 * 老人家属关联表
 */
@Transactional
@Data
public class ElderKinDTO {
    /**
     * 老人用户ID
     */
    @TableId(value = "elder_id", type = IdType.AUTO)
    private Long elderId;

    /**
     * 家属用户ID
     */
    private Long kinId;
    /**
     * 老人姓名
     */
    private String elderName;

    /**
     * 家属姓名
     */
    private String kinName;


}
