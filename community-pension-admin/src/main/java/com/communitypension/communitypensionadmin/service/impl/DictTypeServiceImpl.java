package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.DictType;
import com.communitypension.communitypensionadmin.mapper.DictTypeMapper;
import com.communitypension.communitypensionadmin.service.DictTypeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 字典类型Service实现类
 */
@Service
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {
    /**
     * 根据字典类型查询字典类型信息
     *
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    @Override
    public DictType selectDictTypeByType(String dictType) {
        return getOne(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictType, dictType));
    }
    /**
     * 新增字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public boolean insertDictType(DictType dictType) {
        return save(dictType);
    }
    /**
     * 修改字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public boolean updateDictType(DictType dictType) {
        return updateById(dictType);
    }
    /**
     * 删除字典类型信息
     *
     * @param dictId 字典类型ID
     * @return 结果
     */
    @Override
    public boolean deleteDictTypeById(Long dictId) {
        return removeById(dictId);
    }

    /**
     * 刷新字典缓存
     *
     *
     */
    @Override
    @CacheEvict(value = {"dict-type", "dict-data"}, allEntries = true)
    public void refreshCache() {
        // 清除所有字典缓存
    }
} 