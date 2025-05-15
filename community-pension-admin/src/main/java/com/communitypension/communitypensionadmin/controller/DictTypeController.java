package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.entity.DictType;
import com.communitypension.communitypensionadmin.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 字典类型Controller
 */
@RestController
@Transactional
@RequestMapping("/api/system/dict/type")
public class DictTypeController {
    
    @Autowired
    private DictTypeService dictTypeService;
    
    /**
     * 获取字典类型列表
     */
    @GetMapping("/list")
    public Result<Page<DictType>> list(DictType dictType,
                                     @RequestParam(defaultValue = "1") int current,
                                     @RequestParam(defaultValue = "10") int size) {
        Page<DictType> page = new Page<>(current, size);
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(dictType.getDictName() != null, DictType::getDictName, dictType.getDictName())
                .eq(dictType.getStatus() != null, DictType::getStatus, dictType.getStatus())
                .eq(dictType.getDictType() != null, DictType::getDictType, dictType.getDictType())
                .orderByAsc(DictType::getDictId);
        return Result.success(dictTypeService.page(page, wrapper));
    }
    
    /**
     * 获取字典类型详细信息
     */
    @GetMapping("/{dictId}")
    public Result<DictType> getInfo(@PathVariable Long dictId) {
        return Result.success(dictTypeService.getById(dictId));
    }
    
    /**
     * 新增字典类型
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody DictType dictType) {
        return Result.success(dictTypeService.insertDictType(dictType));
    }
    
    /**
     * 修改字典类型
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody DictType dictType) {
        return Result.success(dictTypeService.updateDictType(dictType));
    }
    
    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictId}")
    public Result<Boolean> remove(@PathVariable Long dictId) {
        return Result.success(dictTypeService.deleteDictTypeById(dictId));
    }
    
    /**
     * 刷新字典缓存
     */
    @DeleteMapping("/refreshCache")
    public Result<Void> refreshCache() {
        dictTypeService.refreshCache();
        return Result.success("刷新缓存成功");
    }
} 