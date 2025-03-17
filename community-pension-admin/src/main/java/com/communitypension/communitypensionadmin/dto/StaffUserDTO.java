package com.communitypension.communitypensionadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社区工作人员与用户信息联合DTO
 */
@Data
public class StaffUserDTO {
    // Staff字段
    private Long id;
    private String name;
    private String department;
    private String position;
    
    // User字段
    private String phone;
    private String email;
    private String address;
    private String avatar;
    private Integer isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    private String remark;
} 