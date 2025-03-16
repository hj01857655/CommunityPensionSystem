package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Kin;
import com.communitypension.communitypensionadmin.service.KinService;
import com.communitypension.communitypensionadmin.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kins")
@CrossOrigin(origins = "*")
public class KinController {
    private final KinService kinService;

    @Autowired
    public KinController(KinService kinService) {
        this.kinService = kinService;
    }

    @GetMapping("")
    public Result<Object> getKins(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {


        Page<Kin> page = kinService.page(new Page<>(current, size));
        return Result.success("查询成功", page);
    }

    @GetMapping("/{id}")
    public Result<Object> getKinById(@PathVariable Long id) {
        Kin kin = kinService.getById(id);
        return kin != null ? Result.success("查询成功", kin) : Result.error(404, "亲属信息不存在");
    }

    @PostMapping
    public Result<Object> createKin(@RequestBody Kin kin) {


        boolean saved = kinService.save(kin);
        return saved ? Result.success("创建成功", kin.getId()) : Result.error(500, "创建失败");
    }

    @PutMapping
    public Result<Object> updateKin(@RequestBody Kin kin) {


        boolean updated = kinService.updateById(kin);
        return updated ? Result.success("更新成功") : Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteKin(@PathVariable Long id) {


        boolean removed = kinService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error(500, "删除失败");
    }
}
