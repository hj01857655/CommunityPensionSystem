package com.communitypension.communitypensionadmin.vo;

import lombok.Data;
import java.util.List;

/**
 * 角色树形结构VO
 */
@Data
public class RoleTreeVO {
    private Long id;
    private String label;
    private List<RoleTreeVO> children;
} 