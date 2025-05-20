package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.pojo.vo.DictDataVO;
import jakarta.servlet.http.HttpServletResponse;
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
     *
     * 获取字典数据列表
     * @param current
     * @param size
     * @param dictType
     * @param dictLabel
     * @param dictValue
     * @param status
     * @return
     */
    @GetMapping("/list")
    public Result<Page<DictDataVO>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String dictLabel,
            @RequestParam(required = false) String dictValue,
            @RequestParam(required = false) String status) {
        Page<DictDataVO> pageVO = dictDataService.getDictDataList(current, size, dictType, dictLabel, dictValue, status);
        return Result.success(pageVO);
    }
    /**
     * 新增字典数据
     * @param dictDataVO
     * @return
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody DictDataVO dictDataVO) {
        dictDataService.createDictData(dictDataVO);
        return Result.success(true);
    }
    /**
     * 修改字典数据
     * @param dictDataVO 字典数据
     * @return
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody DictDataVO dictDataVO) {
        dictDataService.updateDictData(dictDataVO);
        return Result.success(true);
    }

    /**
     * 删除字典数据
     * @param dictCode
     * @return
     */
    @DeleteMapping("/{dictCode}")
    public Result<Boolean> remove(@PathVariable Long dictCode) {
        dictDataService.deleteDictDataById(dictCode);
        return Result.success(true);
    }

    /**
     * 获取字典数据详细信息
     * @param dictCode
     * @return
     */
    @GetMapping("/{dictCode}")
    public Result<DictDataVO> getInfo(@PathVariable Long dictCode) {
        DictDataVO dictDataVO = dictDataService.getDictDataDetail(String.valueOf(dictCode));
        return Result.success(dictDataVO);
    }
    /**
     * 根据字典类型查询字典数据
     * @param dictType
     * @return
     */
    @GetMapping("/type/{dictType}")
    public Result<List<DictDataVO>> getDictDataByType(@PathVariable String dictType) {
        List<DictDataVO> dictDataVOList = dictDataService.getDictDataByType(dictType);
        return Result.success(dictDataVOList);
    }
    /**
     * 导出字典数据
     * @param response 响应对象
     * @param dictType
     * @param dictLabel
     * @param dictValue
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String dictType,
                       @RequestParam(required = false) String dictLabel,
                       @RequestParam(required = false) String dictValue) {
        dictDataService.exportDictData(dictType, dictLabel, dictValue, response);
    }
}