package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康档案Mapper接口
 */
@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
    // 使用MyBatis-Plus提供的标准方法和Lambda查询构建器
    // 不需要自定义方法
}
