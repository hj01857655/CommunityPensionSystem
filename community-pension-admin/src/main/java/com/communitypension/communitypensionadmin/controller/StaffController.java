package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Staff;
import com.communitypension.communitypensionadmin.service.StaffService;
import com.communitypension.communitypensionadmin.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staffs")
@Tag(name = "工作人员管理")

public class StaffController {
    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("")
    @Operation(summary = "获取工作人员列表")
    public Result<Object> getStaffs(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<Staff> page = staffService.page(new Page<>(current, size));
        return Result.success("查询成功", page);
    }

    @GetMapping("/{id}")
    public Result<Object> getStaffById(@PathVariable Long id) {

        

        Staff staff = staffService.getById(id);
        return staff != null ? Result.success("查询成功", staff) : Result.error(404, "工作人员信息不存在");
    }

    @PostMapping
    public Result<Object> createStaff(@RequestBody Staff staff) {
        boolean saved = staffService.save(staff);
        return saved ? Result.success("创建成功", staff.getId()) : Result.error(500, "创建失败");
    }

    @PutMapping
    public Result<Object> updateStaff(@RequestBody Staff staff) {

        

        boolean updated = staffService.updateById(staff);
        return updated ? Result.success("更新成功") : Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteStaff(@PathVariable Long id) {

        

        boolean removed = staffService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error(500, "删除失败");
    }
}
