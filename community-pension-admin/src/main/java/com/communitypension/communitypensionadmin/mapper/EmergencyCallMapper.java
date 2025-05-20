package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.EmergencyCall;
import org.apache.ibatis.annotations.Mapper;

/**
 * 紧急呼叫Mapper接口
 */
@Mapper
public interface EmergencyCallMapper extends BaseMapper<EmergencyCall> {
}
