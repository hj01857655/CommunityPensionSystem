package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体检报告主表实体类
 */
@Data
@TableName("physical_exam_report")
public class PhysicalExamReport {
    /** 体检报告ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 老人ID */
    private Long elderId;

    /** 上传人ID */
    private Long recorderId;

    /** 体检日期 */
    private LocalDate date;

    /** 体检医院 */
    private String hospital;

    /** 主要结论 */
    private String mainResult;

    /** 报告文件URL */
    private String fileUrl;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 