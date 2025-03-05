package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Kin;
import com.communitypension.communitypensionadmin.service.KinService;
import com.communitypension.communitypensionadmin.util.Result;
import com.communitypension.communitypensionadmin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kins")
@CrossOrigin(origins = "http://localhost:8081/")
public class KinController {
    private final KinService kinService;
    private final JwtUtil jwtUtil;

    @Autowired
    public KinController(KinService kinService, JwtUtil jwtUtil) {
        this.kinService = kinService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("")
    public Result<Object> getKins(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {

        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }

        Page<Kin> page = kinService.page(new Page<>(current, size));
        return Result.success("查询成功", page);
    }

    @GetMapping("/{id}")
    public Result<Object> getKinById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }

        Kin kin = kinService.getById(id);
        return kin != null ?
            Result.success("查询成功", kin) :
            Result.error(404, "亲属信息不存在");
    }

    @PostMapping
    public Result<Object> createKin(
            @RequestHeader("Authorization") String token,
            @RequestBody Kin kin) {

        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }

        boolean saved = kinService.save(kin);
        return saved ?
            Result.success("创建成功", kin.getId()) :
            Result.error(500, "创建失败");
    }

    @PutMapping
    public Result<Object> updateKin(
            @RequestHeader("Authorization") String token,
            @RequestBody Kin kin) {

        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }

        boolean updated = kinService.updateById(kin);
        return updated ?
            Result.success("更新成功") :
            Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteKin(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }

        boolean removed = kinService.removeById(id);
        return removed ?
            Result.success("删除成功") :
            Result.error(500, "删除失败");
    }
}
