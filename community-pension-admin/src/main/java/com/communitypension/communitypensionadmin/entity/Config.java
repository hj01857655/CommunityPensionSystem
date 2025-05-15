package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 参数配置表 config
 */
@Data
@TableName("config")
public class Config {
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

    /** 创建者 */
    @TableField("create_by")
    private String createBy;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;

    /** 更新者 */
    @TableField("update_by")
    private String updateBy;

    /** 更新时间 */
    @TableField("update_time")
    private Date updateTime;
    
    /** 备注 */
    @TableField("remark")
    private String remark;

    @Override
    public String toString() {
        return "Config [configId=" + configId + ", configName=" + configName + ", configKey=" + configKey
                + ", configValue=" + configValue + ", configType=" + configType + "]";
    }
} 