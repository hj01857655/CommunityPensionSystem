package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.DictType;

/**
 * 字典类型Service接口
 */
public interface DictTypeService extends IService<DictType> {
    
    /**
     * 根据字典类型查询字典类型信息
     *
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    DictType selectDictTypeByType(String dictType);
    
    /**
     * 新增字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    boolean insertDictType(DictType dictType);
    
    /**
     * 修改字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    boolean updateDictType(DictType dictType);
    
    /**
     * 删除字典类型信息
     *
     * @param dictId 字典类型ID
     * @return 结果
     */
    boolean deleteDictTypeById(Long dictId);

    /**
     * 刷新字典缓存
     */
    void refreshCache();
} 