package com.communitypension.communitypensionadmin.model.query;

import lombok.Data;

@Data
public class PageQuery {
    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
} 