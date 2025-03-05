package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Elder;
import com.communitypension.communitypensionadmin.service.ElderService;
import com.communitypension.communitypensionadmin.util.Result;
import com.communitypension.communitypensionadmin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/elders")
@CrossOrigin(origins = "http://localhost:8081/")
public class ElderController {
    private final ElderService elderService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ElderController(ElderService elderService, JwtUtil jwtUtil) {
        this.elderService = elderService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("")
    public Result<Object> getElders(@RequestHeader("Authorization") String token, @RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            return Result.error(401, status.getError());
        }

        if (status.shouldRefresh()) {
            // 刷新 Token
            String newToken = jwtUtil.refreshToken(token);
            if (newToken != null) {
                // 更新前端的 Token，例如通过响应头返回新的 Token
                return Result.success("Token 已刷新", newToken);
            }
        }

        Page<Elder> page = elderService.page(new Page<>(current, size));
        return Result.success("查询成功", page);
    }

    @GetMapping("/{id}")
    public Result<Object> getElderById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            return Result.error(401, status.getError());
        }

        Elder elder = elderService.getById(id);
        return elder != null ? Result.success("查询成功", elder) : Result.error(404, "老人信息不存在");
    }

    @PostMapping
    public Result<Object> createElder(@RequestHeader("Authorization") String token, @RequestBody Elder elder) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            return Result.error(401, status.getError());
        }

        boolean saved = elderService.save(elder);
        return saved ? Result.success("创建成功", elder.getId()) : Result.error(500, "创建失败");
    }

    @PutMapping
    public Result<Object> updateElder(@RequestHeader("Authorization") String token, @RequestBody Elder elder) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            return Result.error(401, status.getError());
        }

        boolean updated = elderService.updateById(elder);
        return updated ? Result.success("更新成功") : Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteElder(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            return Result.error(401, status.getError());
        }

        boolean removed = elderService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error(500, "删除失败");
    }
}
