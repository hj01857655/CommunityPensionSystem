package com.communitypension.communitypensionadmin.enums;

import lombok.Getter;

@Getter
public enum ServiceOrderStatus {
    PENDING(0, "待审核"),
    ASSIGNED(1, "已派单"),
    IN_PROGRESS(2, "服务中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消"),
    REJECTED(5, "已拒绝");

    private final Integer code;
    private final String description;

    ServiceOrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ServiceOrderStatus getByCode(Integer code) {
        for (ServiceOrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
