package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Elder;
import com.communitypension.communitypensionadmin.entity.Kin;
import com.communitypension.communitypensionadmin.service.ElderService;
import com.communitypension.communitypensionadmin.service.KinService;
import com.communitypension.communitypensionadmin.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kins")

public class KinController {
    private final KinService kinService;
    private final ElderService elderService;

    @Autowired
    public KinController(KinService kinService, ElderService elderService) {
        this.kinService = kinService;
        this.elderService = elderService;
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

    /**
     * 根据老人id查询亲属信息
     * @param kin 亲属信息
     * @return 查询结果
     */
    @PostMapping
    public Result<Object> createKin(@RequestBody Kin kin) {


        boolean saved = kinService.save(kin);
        return saved ? Result.success("创建成功", kin.getId()) : Result.error(500, "创建失败");
    }
    /**
     * 更新亲属信息
     * @param kin 亲属信息
     * @return 更新结果
     */
    @PutMapping
    public Result<Object> updateKin(@RequestBody Kin kin) {


        boolean updated = kinService.updateById(kin);
        return updated ? Result.success("更新成功") : Result.error(500, "更新失败");
    }

    /***
     * 根据id删除亲属信息
     * @param id 亲属id
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Object> deleteKin(@PathVariable Long id) {
        boolean removed = kinService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error(500, "删除失败");
    }

    /**
     * 查询绑定的老人信息
     * @param elderId 老人id
     * @return 查询结果
     */
    @GetMapping("/elder/{elderId}")
// 该方法接收一个路径变量elderId，并返回一个Result<Object>类型的结果
    public Result<Object> getElderByElderId(@PathVariable Long elderId) {
        // 检查elderId是否为空，如果为空则返回错误信息
        if (elderId == null) {
            // 返回一个错误结果，状态码为400，错误信息为"老人ID不能为空"
            return Result.error(400, "老人ID不能为空");
        }
        Elder elder = elderService.lambdaQuery().eq(Elder::getId, elderId).one();
        if (elder == null){
            return Result.error(404, "没有老人与该亲属绑定");
        }

        return Result.success("查询成功", elder);
    }

    /***
     * `查询所有亲属信息
     * @return 查询结果
     */
    @GetMapping("/all")
    public Result<Object> getAllKins() {
        List<Kin> kinList = kinService.list();
        return Result.success("查询成功", kinList);
    }

    /**
     * 更新亲属信息
     * @param kin 亲属信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Object> updateKin(@PathVariable Long id, @RequestBody Kin kin) {
        // 确保请求体中的 ID 与路径参数中的 ID 一致
        if (!id.equals(kin.getId())) {
            return Result.error(400, "请求参数错误");
        }

        boolean updated = kinService.updateById(kin);
        return updated ? Result.success("更新成功") : Result.error(500, "更新失败");
    }

    /**
     * 绑定家属与老人关系
     * @param kinId 家属ID
     * @param elderId 老人ID
     * @return 绑定结果
     */
    @PostMapping("/bind/{kinId}/{elderId}")
    public Result<Object> bindKinToElder(@PathVariable Long kinId, @PathVariable Long elderId) {
        // 检查家属和老人是否存在
        Kin kin = kinService.getById(kinId);
        if (kin == null) {
            return Result.error(404, "家属信息不存在");
        }
        if (!elderService.exists(elderService.lambdaQuery().eq(Elder::getId, elderId))) {
            return Result.error(404, "老人信息不存在");
        }

        // 更新家属的elderId
        kin.setElderId(elderId);
        boolean updated = kinService.updateById(kin);

        return updated ? Result.success("绑定成功") : Result.error(500, "绑定失败");
    }

    /**
     * 解绑家属与老人关系
     * @param kinId 家属ID
     * @return 解绑结果
     */
    @DeleteMapping("/unbind/{kinId}")
    public Result<Object> unbindKinFromElder(@PathVariable Long kinId) {
        Kin kin = kinService.getById(kinId);
        if (kin == null) {
            return Result.error(404, "家属信息不存在");
        }

        // 将elderId设置为null
        kin.setElderId(null);
        boolean updated = kinService.updateById(kin);

        return updated ? Result.success("解绑成功") : Result.error(500, "解绑失败");
    }
}
