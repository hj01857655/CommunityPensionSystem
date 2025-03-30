package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.DictData;
import com.communitypension.communitypensionadmin.vo.DictDataVO;

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
    List<DictDataVO> selectDictDataByType(String dictType);
    
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

    /**
     * 根据字典类型获取字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DictDataVO> getDictDataByType(String dictType);
    
    /**
     * 根据字典类型和字典值获取字典数据
     */
    DictDataVO getDictDataByTypeAndValue(String dictType, String dictValue);
    
    /**
     * 分页查询字典数据列表
     */
    Page<DictDataVO> getDictDataList(Integer pageNum, Integer pageSize, String dictType, String dictLabel, String dictValue);
    
    /**
     * 获取字典数据详情
     */
    DictDataVO getDictDataDetail(String dictCode);
    
    /**
     * 创建字典数据
     */
    void createDictData(DictDataVO dto);
    
    /**
     * 更新字典数据
     */
    void updateDictData(DictDataVO dto);
    
    /**
     * 删除字典数据
     */
    void deleteDictData(String dictCode);
    
    /**
     * 导出字典数据
     */
    void exportDictData(String dictType, String dictLabel, String dictValue, jakarta.servlet.http.HttpServletResponse response);
} 