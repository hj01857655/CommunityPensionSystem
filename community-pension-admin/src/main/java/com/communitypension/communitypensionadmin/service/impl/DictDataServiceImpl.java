package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.DictData;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.DictDataMapper;
import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.vo.DictDataVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典数据Service实现类
 */
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {
    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合
     */
    @Override
    public List<DictDataVO> selectDictDataByType(String dictType) {
        // 判断字典类型是否为空
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
    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合
     */
    @Override
    public List<DictDataVO> getDictDataByType(String dictType) {
        return selectDictDataByType(dictType);
    }
    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public DictDataVO getDictDataByTypeAndValue(String dictType, String dictValue) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
               .eq(DictData::getDictValue, dictValue)
               .eq(DictData::getStatus, "0");
        return convertToVO(getOne(wrapper));
    }
    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        DictDataVO dictData = getDictDataByTypeAndValue(dictType, dictValue);
        return dictData != null ? dictData.getDictLabel() : "";
    }
    /**
     * 新增字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public boolean insertDictData(DictData dictData) {
        return save(dictData);
    }
    /**
     * 修改字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public boolean updateDictData(DictData dictData) {
        return updateById(dictData);
    }
    /**
     * 删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    @Override
    public boolean deleteDictDataById(Long dictCode) {
        return removeById(dictCode);
    }
    /**
     * 分页查询字典数据列表
     */
    @Override
    public Page<DictDataVO> getDictDataList(Integer pageNum, Integer pageSize, String dictType, String dictLabel, String dictValue) {
        // 1. 参数校验
        pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        
        try {
            // 2. 构建查询条件
            LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(StringUtils.hasText(dictType), DictData::getDictType, dictType)
                   .like(StringUtils.hasText(dictLabel), DictData::getDictLabel, dictLabel)
                   .like(StringUtils.hasText(dictValue), DictData::getDictValue, dictValue)
                   // 添加状态条件，默认只查询正常状态
                   .eq(DictData::getStatus, "0")
                   // 添加排序条件
                   .orderByAsc(DictData::getDictSort)
                   .orderByDesc(DictData::getUpdateTime);

            // 3. 执行分页查询
            Page<DictData> page = page(new Page<>(pageNum, pageSize), wrapper);
            
            // 4. 转换为VO对象
            Page<DictDataVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
            List<DictDataVO> voList = page.getRecords().stream()
                    .map(this::convertToVO)
                    .toList();
            voPage.setRecords(voList);
            
            return voPage;
        } catch (Exception e) {
            log.error("获取字典数据列表失败", e);
            throw new BusinessException("获取字典数据列表失败");
        }
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
    /**
     * 更新字典数据
     */
    @Override
    public void updateDictData(DictDataVO dto) {
        // 1. 参数校验
        if (dto == null || dto.getDictCode() == null) {
            throw new BusinessException("字典编码不能为空");
        }
        if (!StringUtils.hasText(dto.getDictLabel())) {
            throw new BusinessException("字典标签不能为空");
        }
        if (!StringUtils.hasText(dto.getDictValue())) {
            throw new BusinessException("字典键值不能为空");
        }
        
        // 2. 检查是否存在重复的字典键值
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dto.getDictType())
               .eq(DictData::getDictValue, dto.getDictValue())
               .ne(DictData::getDictCode, dto.getDictCode());
        if (count(wrapper) > 0) {
            throw new BusinessException("该字典类型下已存在相同的字典键值");
        }
        
        try {
            // 3. 状态更新处理
            if (dto.getStatus() != null) {
                DictData updateData = new DictData();
                updateData.setDictCode(dto.getDictCode());
                updateData.setStatus(dto.getStatus());
                updateData.setUpdateTime(LocalDateTime.now());
                
                boolean success = lambdaUpdate()
                    .eq(DictData::getDictCode, dto.getDictCode())
                    .update(updateData);
                
                if (!success) {
                    throw new BusinessException("更新字典数据状态失败");
                }
                return;
            }
            
            // 4. 全字段更新
            DictData dictData = new DictData();
            BeanUtils.copyProperties(dto, dictData);
            dictData.setUpdateTime(LocalDateTime.now());
            
            boolean success = updateById(dictData);
            if (!success) {
                throw new BusinessException("更新字典数据失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新字典数据时发生错误", e);
            throw new BusinessException("更新字典数据时发生系统错误");
        }
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