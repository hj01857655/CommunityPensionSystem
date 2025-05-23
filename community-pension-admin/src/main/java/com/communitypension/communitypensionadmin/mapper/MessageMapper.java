package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 消息Mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 影响行数
     */
    @Update("UPDATE message SET `read` = true WHERE id = #{id}")
    int markAsRead(@Param("id") Long id);
    
    /**
     * 标记用户所有消息为已读
     * @param receiver 接收者
     * @return 影响行数
     */
    @Update("UPDATE message SET `read` = true WHERE receiver = #{receiver}")
    int markAllAsRead(@Param("receiver") String receiver);
}
