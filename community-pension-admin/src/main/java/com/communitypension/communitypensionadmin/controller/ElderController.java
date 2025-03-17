package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Elder;
import com.communitypension.communitypensionadmin.entity.HealthRecords;
import com.communitypension.communitypensionadmin.entity.Kin;
import com.communitypension.communitypensionadmin.service.ElderService;
import com.communitypension.communitypensionadmin.service.KinService;
import com.communitypension.communitypensionadmin.utils.Result;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 老人信息管理控制器
 * 提供老人基本信息的增删改查、批量操作等功能
 */
@RestController
@RequestMapping("/api/elders")

public class ElderController {
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(ElderController.class);
    private final ElderService elderService;
    private final KinService kinService;

    @Autowired
    public ElderController(ElderService elderService, KinService kinService) {
        this.elderService = elderService;
        this.kinService = kinService;
    }

    /**
     * 分页查询老人信息列表
     *
     * @param current         当前页码
     * @param size            每页数量
     * @param name            老人姓名（可选）
     * @param idCard          身份证号（可选）
     * @param healthCondition 健康状况（可选）
     * @return 分页查询结果
     */
    @GetMapping
    public Result<Object> getElders(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) String name, @RequestParam(required = false) String idCard, @RequestParam(required = false) String healthCondition) {
        logger.info("分页查询老人信息列表，参数：current = {}, size = {}, name = {}, idCard = {}, healthCondition = {}", current, size, name, idCard, healthCondition);
        // 参数校验
        if (current <= 0 || size <= 0) {
            return Result.error("分页参数不合法");
        }

        LambdaQueryWrapper<Elder> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Elder::getName, name);
        }
        if (StringUtils.hasText(idCard)) {
            queryWrapper.like(Elder::getIdCard, idCard);
        }
        if (StringUtils.hasText(healthCondition)) {
            queryWrapper.like(Elder::getHealthCondition, healthCondition);
        }
        queryWrapper.orderByAsc(Elder::getId);

        try {
            Page<Elder> page = elderService.page(new Page<>(current, size), queryWrapper);
            // 对身份证号进行脱敏处理
            page.getRecords().forEach(elder -> {
                String id = elder.getIdCard();
                if (StringUtils.hasText(id)) {
                    elder.setIdCard(id.substring(0, 4) + "********" + id.substring(id.length() - 4));
                }
            });
            return Result.success("查询老人成功", page);
        } catch (Exception e) {
            // 异常处理
            return Result.error("查询过程中发生错误");
        }
    }


    /**
     * 获取未绑定家属的老人列表
     *
     * @return 未绑定家属的老人列表
     */
    @GetMapping("/unbound")
    public Result<List<Elder>> getUnboundElders() {
        // 查询所有已绑定的elderId
        List<Long> boundElderIds = kinService.lambdaQuery()
            .select(Kin::getElderId)
            .isNotNull(Kin::getElderId)
            .list()
            .stream()
            .map(Kin::getElderId)
            .collect(Collectors.toList());

        // 查询未绑定的老人
        List<Elder> unboundElders = elderService.lambdaQuery()
            .notIn(Elder::getId, boundElderIds)
            .list();

        // 使用两个参数的 success 方法
        return Result.success(200, "查询成功", unboundElders);
    }

    /**
     * 根据ID查询老人信息
     *
     * @param id 老人ID
     * @return 老人详细信息
     */
    @GetMapping("/{id}")
    public Result<Object> getElderById(@PathVariable Long id) {
        Elder elder = elderService.getById(id);
        if (elder != null) {
            String idCard = elder.getIdCard();
            if (StringUtils.hasText(idCard)) {
                elder.setIdCard(idCard.substring(0, 4) + "********" + idCard.substring(idCard.length() - 4));
            }
            return Result.success("查询成功", elder);
        } else {
            return Result.error(404, "老人信息不存在");
        }
    }

    /**
     * 创建老人信息
     *
     * @param elder 老人信息对象
     * @return 创建结果
     */
    @PostMapping
    public Result<Object> createElder(@Valid @RequestBody Elder elder) {
        try {
            boolean saved = elderService.save(elder);
            return saved ? Result.success("创建成功") : Result.error(500, "创建失败");
        } catch (Exception e) {
            return Result.error(500, "创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新老人信息
     *
     * @param id    老人ID
     * @param elder 更新的老人信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Object> updateElder(@PathVariable Long id, @Valid @RequestBody Elder elder) {
        try {
            elder.setId(id);
            boolean updated = elderService.updateById(elder);
            return updated ? Result.success("更新成功") : Result.error(500, "更新失败");
        } catch (Exception e) {
            return Result.error(500, "更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除老人信息
     *
     * @param id 老人ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Object> deleteElder(@PathVariable Long id) {
        try {
            boolean removed = elderService.removeById(id);
            return removed ? Result.success("删除成功") : Result.error(500, "删除失败");
        } catch (Exception e) {
            return Result.error(500, "删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量创建老人信息
     *
     * @param elders 老人信息列表
     * @return 批量创建结果
     */
    @PostMapping("/batch")
    public Result<Object> batchCreateElders(@Valid @RequestBody List<Elder> elders) {
        try {
            boolean saved = elderService.saveBatch(elders);
            return saved ? Result.success("批量创建成功") : Result.error(500, "批量创建失败");
        } catch (Exception e) {
            return Result.error(500, "批量创建失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除老人信息
     *
     * @param ids 老人ID列表
     * @return 批量删除结果
     */
    @DeleteMapping("/batch")
    public Result<Object> batchDeleteElders(@RequestBody List<Long> ids) {
        try {
            boolean removed = elderService.removeByIds(ids);
            return removed ? Result.success("批量删除成功") : Result.error(500, "批量删除失败");
        } catch (Exception e) {
            return Result.error(500, "批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取老人健康档案
     *
     * @param id 老人ID
     * @return 健康档案信息
     */
    @GetMapping("/{id}/health-records")
    public Result<Object> getHealthRecords(@PathVariable Long id) {
        try {
            var healthRecords = elderService.getHealthRecords(id);
            if (healthRecords != null) {
                return Result.success("获取健康档案成功", healthRecords);
            } else {
                return Result.error(404, "健康档案不存在");
            }
        } catch (Exception e) {
            return Result.error(500, "获取健康档案失败：" + e.getMessage());
        }
    }

    /**
     * 更新老人健康档案
     *
     * @param id            老人ID
     * @param healthRecords 健康档案信息
     * @return 更新结果
     */
    @PutMapping("/{id}/health-records")
    public Result<Object> updateHealthRecords(@PathVariable Long id, @Valid @RequestBody HealthRecords healthRecords) {
        try {
            boolean updated = elderService.updateHealthRecords(id, healthRecords);
            return updated ? Result.success("更新健康档案成功") : Result.error(500, "更新健康档案失败");
        } catch (Exception e) {
            return Result.error(500, "更新健康档案失败：" + e.getMessage());
        }
    }
}