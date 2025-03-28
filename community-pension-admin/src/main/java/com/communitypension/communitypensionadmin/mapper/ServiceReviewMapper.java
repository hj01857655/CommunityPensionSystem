package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.communitypension.communitypensionadmin.entity.ServiceReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceReviewMapper extends BaseMapper<ServiceReview> {
    
    /**
     * 分页查询评价列表
     * 注意：此方法在XML中实现，这里只需要声明接口
     */

    IPage<ServiceReview> selectReviewPage(IPage<ServiceReview> page, @Param("review") ServiceReview review);
    
    /**
     * 检查评价是否存在
     * 注意：此方法在XML中实现，这里只需要声明接口
     */
    @Select("SELECT COUNT(*) > 0 FROM service_review WHERE order_id = #{orderId} AND user_id = #{userId}")
    boolean checkReviewExists(@Param("orderId") Long orderId, @Param("userId") Long userId);

    /**
     * 查询服务平均评分
     * @param serviceId 服务ID
     * @return 平均评分
     */
    @Select("SELECT AVG(rating) FROM service_review WHERE order_id = #{serviceId}")
    Double selectAverageRating(@Param("serviceId") Long serviceId);
} 