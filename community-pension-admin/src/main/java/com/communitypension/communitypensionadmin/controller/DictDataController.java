package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.entity.DictData;
import com.communitypension.communitypensionadmin.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据Controller
 */
@RestController
@RequestMapping("/api/system/dict/data")
public class DictDataController {
    
    @Autowired
    private DictDataService dictDataService;
    
    /**
     * 获取字典数据列表
     */
    @GetMapping("/list")
    public Result<Page<DictData>> list(DictData dictData,
                                     @RequestParam(defaultValue = "1") int current,
                                     @RequestParam(defaultValue = "10") int size) {
        Page<DictData> page = new Page<>(current, size);
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dictData.getDictType() != null, DictData::getDictType, dictData.getDictType())
                .like(dictData.getDictLabel() != null, DictData::getDictLabel, dictData.getDictLabel())
                .eq(dictData.getStatus() != null, DictData::getStatus, dictData.getStatus())
                .orderByAsc(DictData::getDictSort);
        return Result.success(dictDataService.page(page, wrapper));
    }
    
    /**
     * 获取字典数据详细信息
     */
    @GetMapping("/{dictCode}")
    public Result<DictData> getInfo(@PathVariable Long dictCode) {
        return Result.success(dictDataService.getById(dictCode));
    }
    
    /**
     * 根据字典类型获取字典数据
     */
    @GetMapping("/type/{dictType}")
    public Result<List<DictData>> getDictDataByType(@PathVariable String dictType) {
        return Result.success(dictDataService.selectDictDataByType(dictType));
    }
    
    /**
     * 新增字典数据
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody DictData dictData) {
        return Result.success(dictDataService.insertDictData(dictData));
    }
    
    /**
     * 修改字典数据
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody DictData dictData) {
        return Result.success(dictDataService.updateDictData(dictData));
    }
    
    /**
     * 删除字典数据
     */
    @DeleteMapping("/{dictCode}")
    public Result<Boolean> remove(@PathVariable Long dictCode) {
        return Result.success(dictDataService.deleteDictDataById(dictCode));
    }
} 