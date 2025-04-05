package com.communitypension.communitypensionadmin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动签到DTO
 */
@Data
public class ActivityCheckInDTO {

    /**
     * 签到ID
     */
    private Long id;

    /**
     * 报名ID
     */
    @NotNull(message = "报名ID不能为空")
    private Long registerId;

    /**
     * 签到人ID（老人本人或家属）
     */
    @NotNull(message = "签到人ID不能为空")
    private Long checkInUserId;

    /**
     * 是否代签：0-本人签到，1-他人代签
     */
    private Integer isProxyCheckIn;

    /**
     * 签退时间
     */
    private String signOutTime;

    /**
     * 备注信息
     */
    private String remarks;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegisterId() {
        return registerId;
    }

    public void setRegisterId(Long registerId) {
        this.registerId = registerId;
    }

    public Long getCheckInUserId() {
        return checkInUserId;
    }

    public void setCheckInUserId(Long checkInUserId) {
        this.checkInUserId = checkInUserId;
    }

    public Integer getIsProxyCheckIn() {
        return isProxyCheckIn;
    }

    public void setIsProxyCheckIn(Integer isProxyCheckIn) {
        this.isProxyCheckIn = isProxyCheckIn;
    }

    public String getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(String signOutTime) {
        this.signOutTime = signOutTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
