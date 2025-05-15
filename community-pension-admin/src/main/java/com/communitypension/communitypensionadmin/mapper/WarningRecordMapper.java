package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.WarningRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预警记录数据访问层
 */
@Mapper
public interface WarningRecordMapper extends BaseMapper<WarningRecord> {
    // 可以添加自定义查询方法
} 