package com.communitypension.communitypensionadmin.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 更新活动的数据传输对象
 * <p>
 * 用于接收前端更新活动时传来的数据，进行数据验证和传输
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivityDTO {
    
    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long id;
    
    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    @Size(max = 100, message = "活动标题长度不能超过100个字符")
    private String title;
    
    /**
     * 活动类型（字典类型：activity_type）
     */
    @NotBlank(message = "活动类型不能为空")
    private String type;
    
    /**
     * 活动描述
     */
    @NotBlank(message = "活动描述不能为空")
    private String description;
    
    /**
     * 活动封面图片URL
     */
    private String coverImage;
    
    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    
    /**
     * 活动地点
     */
    @NotBlank(message = "活动地点不能为空")
    @Size(max = 200, message = "活动地点长度不能超过200个字符")
    private String location;
    
    /**
     * 最大参与人数
     */
    @NotNull(message = "人数上限不能为空")
    @Min(value = 1, message = "人数上限必须大于0")
    private Integer maxParticipants;
    
    /**
     * 组织者ID
     */
    @NotNull(message = "组织者ID不能为空")
    private Long organizerId;
    
    /**
     * 状态：0-筹备中，1-报名中，2-进行中，3-已结束，4-已取消
     */
    private Integer status;
}
