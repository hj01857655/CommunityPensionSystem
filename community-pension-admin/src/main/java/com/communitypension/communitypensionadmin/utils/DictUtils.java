package com.communitypension.communitypensionadmin.utils;

import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.pojo.vo.DictDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典工具类
 */
@Component
public class DictUtils {

    private static DictDataService dictDataService;

    /**
     * 获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue) {
        List<DictDataVO> dictDataList = dictDataService.getDictDataByType(dictType);
        return dictDataList.stream()
                .filter(dict -> dict.getDictValue().equals(dictValue))
                .map(DictDataVO::getDictLabel)
                .findFirst()
                .orElse(dictValue);
    }

    /**
     * 获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel) {
        List<DictDataVO> dictDataList = dictDataService.getDictDataByType(dictType);
        return dictDataList.stream()
                .filter(dict -> dict.getDictLabel().equals(dictLabel))
                .map(DictDataVO::getDictValue)
                .findFirst()
                .orElse(dictLabel);
    }

    /**
     * 获取字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    public static List<DictDataVO> getDictData(String dictType) {
        return dictDataService.getDictDataByType(dictType);
    }

    /**
     * 获取字典数据Map
     *
     * @param dictType 字典类型
     * @return 字典数据Map
     */
    public static Map<String, String> getDictMap(String dictType) {
        List<DictDataVO> dictDataList = dictDataService.getDictDataByType(dictType);
        return dictDataList.stream()
                .collect(Collectors.toMap(DictDataVO::getDictValue, DictDataVO::getDictLabel));
    }

    @Autowired
    public void setDictDataService(DictDataService dictDataService) {
        DictUtils.dictDataService = dictDataService;
    }

} 