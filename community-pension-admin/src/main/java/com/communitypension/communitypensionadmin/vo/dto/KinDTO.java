package com.communitypension.communitypensionadmin.vo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KinDTO extends UserDTO {
    /**
     * 家属绑定的老人Id
     */
    private Integer elderId;

    /**
     * 家属与老人关系
     */
    private String relationship;


}
