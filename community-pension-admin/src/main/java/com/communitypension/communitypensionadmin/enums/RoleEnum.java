package com.communitypension.communitypensionadmin.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ELDER(1L, "老人"),
    KIN(2L, "家属"),
    STAFF(3L, "社区工作人员"),
    ADMIN(4L, "管理员"),
    VISITOR(5L, "访客");

    private final Long id;
    private final String description;

    RoleEnum(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public static boolean isFrontendRole(Long roleId) {
        return roleId != null && (roleId.equals(ELDER.id) || roleId.equals(KIN.id));
    }

    public static boolean isBackendRole(Long roleId) {
        return roleId != null && (roleId.equals(STAFF.id) || roleId.equals(ADMIN.id));
    }

    public static String getDescription(Long roleId) {
        for (RoleEnum role : values()) {
            if (role.getId().equals(roleId)) {
                return role.getDescription();
            }
        }
        return "未知角色";
    }
}