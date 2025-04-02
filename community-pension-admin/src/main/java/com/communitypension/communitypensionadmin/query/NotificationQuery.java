package com.communitypension.communitypensionadmin.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.communitypension.communitypensionadmin.query.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationQuery extends PageQuery {
    private String title;
    private Integer status;
} 