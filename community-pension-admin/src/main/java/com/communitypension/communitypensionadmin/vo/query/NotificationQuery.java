package com.communitypension.communitypensionadmin.vo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationQuery extends com.communitypension.communitypensionadmin.model.query.PageQuery {
    private String title;
    private Integer status;
} 