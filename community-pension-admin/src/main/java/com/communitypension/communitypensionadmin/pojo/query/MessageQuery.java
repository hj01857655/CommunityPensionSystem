package com.communitypension.communitypensionadmin.pojo.query;

import lombok.Data;

/**
 * 消息查询对象
 */
@Data
public class MessageQuery {
    
    /**
     * 当前页码
     */
    private Integer current = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
    
    /**
     * 消息类型：all-全部, read-已读, unread-未读
     */
    private String type = "all";
    
    /**
     * 接收者ID
     */
    private String receiver;
    
    /**
     * 消息内容关键词
     */
    private String keyword;
}
