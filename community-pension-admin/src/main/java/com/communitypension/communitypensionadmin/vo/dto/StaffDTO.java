package com.communitypension.communitypensionadmin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StaffDTO extends UserDTO {
    /**
     * 社区工作人员所在部门
     */
    private String department;

    /**
     * 社区工作人员岗位
     */
    private String position;

}
