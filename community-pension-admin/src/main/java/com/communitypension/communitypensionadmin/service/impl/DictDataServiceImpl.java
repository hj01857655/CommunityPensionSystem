package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.DictData;
import com.communitypension.communitypensionadmin.mapper.DictDataMapper;
import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.vo.DictDataVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * 字典数据Service实现类
 */
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {
    
    @Override
    public List<DictDataVO> selectDictDataByType(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            return List.of();
        }
        
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, 0) // 只查询正常状态的数据
                .orderByAsc(DictData::getDictSort);
        return list(wrapper).stream()
                .map(this::convertToVO)
                .toList();
    }
    
    @Override
    public List<DictDataVO> getDictDataByType(String dictType) {
        return selectDictDataByType(dictType);
    }
    
    @Override
    public DictDataVO getDictDataByTypeAndValue(String dictType, String dictValue) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
               .eq(DictData::getDictValue, dictValue)
               .eq(DictData::getStatus, "0");
        return convertToVO(getOne(wrapper));
    }
    
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        DictDataVO dictData = getDictDataByTypeAndValue(dictType, dictValue);
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
    
    @Override
    public Page<DictDataVO> getDictDataList(Integer pageNum, Integer pageSize, String dictType, String dictLabel, String dictValue) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dictType), DictData::getDictType, dictType)
               .like(StringUtils.hasText(dictLabel), DictData::getDictLabel, dictLabel)
               .like(StringUtils.hasText(dictValue), DictData::getDictValue, dictValue)
               .orderByAsc(DictData::getDictSort);
        
        Page<DictData> page = page(new Page<>(pageNum, pageSize), wrapper);
        Page<DictDataVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        
        List<DictDataVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .toList();
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public DictDataVO getDictDataDetail(String dictCode) {
        DictData dictData = getById(dictCode);
        return convertToVO(dictData);
    }
    
    @Override
    public void createDictData(DictDataVO dto) {
        DictData dictData = new DictData();
        BeanUtils.copyProperties(dto, dictData);
        save(dictData);
    }
    
    @Override
    public void updateDictData(DictDataVO dto) {
        DictData dictData = new DictData();
        BeanUtils.copyProperties(dto, dictData);
        updateById(dictData);
    }
    
    @Override
    public void deleteDictData(String dictCode) {
        removeById(dictCode);
    }
    
    @Override
    public void exportDictData(String dictType, String dictLabel, String dictValue, HttpServletResponse response) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dictType), DictData::getDictType, dictType)
               .like(StringUtils.hasText(dictLabel), DictData::getDictLabel, dictLabel)
               .like(StringUtils.hasText(dictValue), DictData::getDictValue, dictValue)
               .orderByAsc(DictData::getDictSort);
        
        List<DictData> list = list(wrapper);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("字典数据");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("字典编码");
            headerRow.createCell(1).setCellValue("字典标签");
            headerRow.createCell(2).setCellValue("字典键值");
            headerRow.createCell(3).setCellValue("字典类型");
            headerRow.createCell(4).setCellValue("样式属性");
            headerRow.createCell(5).setCellValue("表格字典样式");
            headerRow.createCell(6).setCellValue("是否默认");
            headerRow.createCell(7).setCellValue("状态");
            
            // 填充数据
            int rowNum = 1;
            for (DictData dictData : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dictData.getDictCode());
                row.createCell(1).setCellValue(dictData.getDictLabel());
                row.createCell(2).setCellValue(dictData.getDictValue());
                row.createCell(3).setCellValue(dictData.getDictType());
                row.createCell(4).setCellValue(dictData.getCssClass());
                row.createCell(5).setCellValue(dictData.getListClass());
                row.createCell(6).setCellValue(dictData.getIsDefault());
                row.createCell(7).setCellValue(dictData.getStatus());
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=dict_data.xlsx");
            
            // 写入响应流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出字典数据失败", e);
        }
    }
    
    private DictDataVO convertToVO(DictData dictData) {
        if (dictData == null) {
            return null;
        }
        DictDataVO vo = new DictDataVO();
        BeanUtils.copyProperties(dictData, vo);
        return vo;
    }
} 