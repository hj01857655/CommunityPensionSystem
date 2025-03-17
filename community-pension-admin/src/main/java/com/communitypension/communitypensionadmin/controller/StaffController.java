package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.dto.StaffUserDTO;
import com.communitypension.communitypensionadmin.entity.Staff;
import com.communitypension.communitypensionadmin.service.StaffService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@Tag(name = "社区工作人员管理")
@RequiredArgsConstructor
public class StaffController {
    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);
    
    private final StaffService staffService;
    private final UserService userService;
    
    @Operation(summary = "分页查询社区工作人员列表")
    @GetMapping
    public Result<Object> getStaffList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name
    ) {
        logger.info("分页查询社区工作人员列表，参数：current = {}, size = {}, name = {}", current, size, name);
        
        if (current <= 0 || size <= 0) {
            return Result.error("分页参数不合法");
        }

        // 使用Service层封装的联表查询
        Page<StaffUserDTO> resultPage = staffService.queryStaffWithUserInfo(
                new Page<>(current, size), name);

        return Result.success("查询社区工作人员成功", resultPage);
    }
    
    @Operation(summary = "查询单个社区工作人员")
    @GetMapping("/{id}")
    public Result<Object> getStaff(@PathVariable Long id) {
        logger.info("查询社区工作人员: ID={}", id);
        Staff staff = staffService.getById(id);
        if (staff != null) {
            return Result.success("查询成功", staff);
        } else {
            return Result.error(404, "社区工作人员不存在");
        }
    }
    
    @Operation(summary = "添加社区工作人员")
    @PostMapping
    public Result<Object> addStaff(@RequestBody Staff staff) {
        logger.info("添加社区工作人员: {}", staff);
        if (staffService.save(staff)) {
            return Result.success("添加成功", staff);
        } else {
            return Result.error("添加失败");
        }
    }
    
    @Operation(summary = "更新社区工作人员")
    @PutMapping("/{id}")
    public Result<Object> updateStaff(@PathVariable Long id, @RequestBody Staff staff) {
        logger.info("更新社区工作人员: ID={}, Staff={}", id, staff);
        staff.setId(id);
        if (staffService.updateById(staff)) {
            return Result.success("更新成功", staff);
        } else {
            return Result.error("更新失败");
        }
    }
    
    @Operation(summary = "删除社区工作人员")
    @DeleteMapping("/{id}")
    public Result<Object> deleteStaff(@PathVariable Long id) {
        logger.info("删除社区工作人员: ID={}", id);
        if (staffService.removeById(id)) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @Operation(summary = "获取所有社区工作人员")
    @GetMapping("/all")
    public Result<List<Staff>> getAllStaffs() {
        logger.info("获取所有社区工作人员");
        List<Staff> staffs = staffService.list();
        return Result.success("查询成功", staffs);
    }
}
