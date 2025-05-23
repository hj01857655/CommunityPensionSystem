package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置表 config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config")
public class Config extends BaseEntity {
    /** 参数主键 */
    @TableId(value = "config_id", type = IdType.AUTO)
    private Integer configId;

    /** 参数名称 */
    @TableField("config_name")
    private String configName;

    /** 参数键名 */
    @TableField("config_key")
    private String configKey;

    /** 参数键值 */
    @TableField("config_value")
    private String configValue;

    /** 系统内置（Y是 N否） */
    @TableField("config_type")
    private String configType;

    // 注意：createBy, createTime, updateBy, updateTime, remark 字段已从BaseEntity继承

    @Override
    public String toString() {
        return "Config [configId=" + configId + ", configName=" + configName + ", configKey=" + configKey
                + ", configValue=" + configValue + ", configType=" + configType + "]";
    }
} 