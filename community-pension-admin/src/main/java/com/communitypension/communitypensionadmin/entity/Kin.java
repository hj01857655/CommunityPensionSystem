package com.communitypension.communitypensionadmin.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("kin")
public class Kin {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String phone;
    private String address;
    private String relationship;
    private Long elderId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
}


