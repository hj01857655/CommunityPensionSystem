package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
    /**
     * 获取工作人员信息（联表查询user表）- 使用注解实现
     * @param page 分页对象
     * @param name 姓名（可选）
     * @return 分页数据
     */
    @Select({
        "<script>",
        "SELECT ",
        "  s.id,",
        "  s.name,",
        "  s.department,",
        "  s.position,",
        "  u.phone,",
        "  u.email,",
        "  u.address,",
        "  u.avatar,",
        "  u.is_active,",
        "  u.create_time,",
        "  u.update_time,",
        "  u.remark",
        "FROM ",
        "  staff s",
        "LEFT JOIN ",
        "  user u ON s.id = u.staff_id",
        "<where>",
        "  <if test='name != null and name != \"\"'>",
        "    AND s.name LIKE CONCAT('%', #{name}, '%')",
        "  </if>",
        "</where>",
        "ORDER BY s.id ASC",
        "</script>"
    })
    Page<Map<String, Object>> getStaffWithUserInfo(Page<Staff> page, @Param("name") String name);
}
