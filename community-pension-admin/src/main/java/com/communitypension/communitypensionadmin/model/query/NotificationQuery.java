package com.communitypension.communitypensionadmin.model.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationQuery extends PageQuery {
    private String title;
    private Integer status;
} 