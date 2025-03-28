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
    
    @Override
    public DictType selectDictTypeByType(String dictType) {
        return getOne(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictType, dictType));
    }
    
    @Override
    public boolean insertDictType(DictType dictType) {
        return save(dictType);
    }
    
    @Override
    public boolean updateDictType(DictType dictType) {
        return updateById(dictType);
    }
    
    @Override
    public boolean deleteDictTypeById(Long dictId) {
        return removeById(dictId);
    }

    /**
     * 刷新字典缓存
     */
    @Override
    @CacheEvict(value = {"dict-type", "dict-data"}, allEntries = true)
    public void refreshCache() {
        // 清除所有字典缓存
    }
} 