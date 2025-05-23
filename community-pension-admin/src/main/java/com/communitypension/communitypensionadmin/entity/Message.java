package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message")
public class Message extends BaseEntity {
    
    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 发送者
     */
    private String sender;
    
    /**
     * 接收者
     */
    private String receiver;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 发送者头像
     */
    private String avatar;
    
    /**
     * 消息时间
     */
    private LocalDateTime time;
    
    /**
     * 是否已读
     */
    @TableField(value = "`read`")
    private Boolean isRead;
    
    /**
     * 消息类型（系统消息、用户消息等）
     */
    private String type;
    
    // 注意：createBy, createTime, updateBy, updateTime, remark 字段已从BaseEntity继承
}
