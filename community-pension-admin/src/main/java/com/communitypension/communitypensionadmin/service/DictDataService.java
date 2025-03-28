package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.DictData;

import java.util.List;

/**
 * 字典数据Service接口
 */
public interface DictDataService extends IService<DictData> {
    
    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合
     */
    List<DictData> selectDictDataByType(String dictType);
    
    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    String selectDictLabel(String dictType, String dictValue);
    
    /**
     * 新增字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    boolean insertDictData(DictData dictData);
    
    /**
     * 修改字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    boolean updateDictData(DictData dictData);
    
    /**
     * 删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    boolean deleteDictDataById(Long dictCode);
} 