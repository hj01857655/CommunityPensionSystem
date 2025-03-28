package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.DictData;
import com.communitypension.communitypensionadmin.mapper.DictDataMapper;
import com.communitypension.communitypensionadmin.service.DictDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据Service实现类
 */
@Service
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {
    
    @Override
    public List<DictData> selectDictDataByType(String dictType) {
        return list(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDictType, dictType)
                .orderByAsc(DictData::getDictSort));
    }
    
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        DictData dictData = getOne(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDictType, dictType)
                .eq(DictData::getDictValue, dictValue));
        return dictData != null ? dictData.getDictLabel() : "";
    }
    
    @Override
    public boolean insertDictData(DictData dictData) {
        return save(dictData);
    }
    
    @Override
    public boolean updateDictData(DictData dictData) {
        return updateById(dictData);
    }
    
    @Override
    public boolean deleteDictDataById(Long dictCode) {
        return removeById(dictCode);
    }
} 