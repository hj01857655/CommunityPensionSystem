package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.PhysicalExamReport;
import com.communitypension.communitypensionadmin.entity.PhysicalExamReportItem;
import com.communitypension.communitypensionadmin.pojo.dto.PhysicalExamReportCreateDTO;
import com.communitypension.communitypensionadmin.pojo.dto.PhysicalExamReportUpdateDTO;
import com.communitypension.communitypensionadmin.pojo.vo.PhysicalExamReportDetailVO;
import com.communitypension.communitypensionadmin.service.PhysicalExamReportItemService;
import com.communitypension.communitypensionadmin.service.PhysicalExamReportService;
import com.communitypension.communitypensionadmin.utils.FileUploadUtil;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.constant.PhysicalExamCategories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 体检报告相关接口
 */
@RestController
@RequestMapping("/api/physicalExamReport")
public class PhysicalExamReportController {
    @Autowired
    private PhysicalExamReportService reportService;
    @Autowired
    private PhysicalExamReportItemService itemService;
    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 体检报告文件上传
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件大小
            long fileSize = file.getSize();
            long maxFileSize = 10 * 1024 * 1024; // 10MB
            
            if (fileSize > maxFileSize) {
                return Result.error("文件大小超过限制，最大允许10MB");
            }
            
            String fileUrl = fileUploadUtil.uploadFile(file, "physical_exam");
            return Result.success("上传成功", fileUrl);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 查询某老人所有体检报告
     */
    @GetMapping("/list")
    public Result<List<PhysicalExamReport>> list(@RequestParam Long elderId) {
        return Result.success(reportService.getReportsByElderId(elderId));
    }

    /**
     * 查询体检报告详情（主表+子表）
     */
    @GetMapping("/{id}")
    public Result<PhysicalExamReportDetailVO> detail(@PathVariable Long id) {
        PhysicalExamReport report = reportService.getById(id);
        if (report == null) {
            return Result.error("体检报告不存在");
        }
        List<PhysicalExamReportItem> items = itemService.getItemsByReportId(id);
        PhysicalExamReportDetailVO vo = new PhysicalExamReportDetailVO();
        vo.setReport(report);
        vo.setItems(items);
        return Result.success(vo);
    }

    /**
     * 查询体检报告详情，并按分类返回项目
     */
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detailWithCategories(@PathVariable Long id) {
        PhysicalExamReport report = reportService.getById(id);
        if (report == null) {
            return Result.error("体检报告不存在");
        }
        
        List<PhysicalExamReportItem> items = itemService.getItemsByReportId(id);
        
        // 按分类分组
        Map<String, List<PhysicalExamReportItem>> categorizedItems = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory() == null ? PhysicalExamCategories.OTHER : item.getCategory()
                ));
        
        // 确保所有分类都存在，即使没有数据
        for (String category : PhysicalExamCategories.getAllCategories()) {
            if (!categorizedItems.containsKey(category)) {
                categorizedItems.put(category, List.of());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("report", report);
        result.put("itemsByCategory", categorizedItems);
        
        return Result.success(result);
    }

    /**
     * 新增体检报告（主表+子表）
     */
    @PostMapping("/add")
    @Transactional
    public Result<?> add(@RequestBody PhysicalExamReportCreateDTO dto) {
        PhysicalExamReport report = new PhysicalExamReport();
        report.setElderId(dto.getElderId());
        report.setRecorderId(dto.getRecorderId());
        report.setDate(dto.getDate());
        report.setHospital(dto.getHospital());
        report.setMainResult(dto.getMainResult());
        report.setFileUrl(dto.getFileUrl());
        
        // 设置新增字段
        report.setReportNo(dto.getReportNo());
        report.setElderName(dto.getElderName());
        report.setGender(dto.getGender());
        report.setAge(dto.getAge());
        
        reportService.save(report);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (PhysicalExamReportCreateDTO.PhysicalExamReportItemDTO itemDTO : dto.getItems()) {
                PhysicalExamReportItem item = new PhysicalExamReportItem();
                item.setReportId(report.getId());
                item.setItemName(itemDTO.getItemName());
                item.setItemValue(itemDTO.getItemValue());
                item.setItemUnit(itemDTO.getItemUnit());
                item.setAbnormalFlag(itemDTO.getAbnormalFlag());
                
                // 设置新增字段
                item.setCategory(itemDTO.getCategory());
                item.setRemark(itemDTO.getRemark());
                item.setNormalRange(itemDTO.getNormalRange());
                
                itemService.save(item);
            }
        }
        return Result.created(report.getId());
    }

    /**
     * 删除体检报告（主表+子表）
     */
    @DeleteMapping("/{id}")
    @Transactional
    public Result<?> delete(@PathVariable Long id) {
        PhysicalExamReport report = reportService.getById(id);
        if (report == null) {
            return Result.error("体检报告不存在");
        }
        itemService.remove(
                new LambdaQueryWrapper<PhysicalExamReportItem>()
                        .eq(PhysicalExamReportItem::getReportId, id)
        );
        reportService.removeById(id);
        return Result.deleted();
    }

    /**
     * 分页查询体检报告
     */
    @GetMapping("/page")
    public Result<IPage<PhysicalExamReport>> page(
            @RequestParam Long elderId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String hospital,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd) {
        
        Page<PhysicalExamReport> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PhysicalExamReport> queryWrapper = new LambdaQueryWrapper<PhysicalExamReport>()
                .eq(PhysicalExamReport::getElderId, elderId)
                .like(hospital != null && !hospital.isEmpty(), PhysicalExamReport::getHospital, hospital)
                .ge(dateStart != null && !dateStart.isEmpty(), PhysicalExamReport::getDate, dateStart)
                .le(dateEnd != null && !dateEnd.isEmpty(), PhysicalExamReport::getDate, dateEnd)
                .orderByDesc(PhysicalExamReport::getDate);
                
        IPage<PhysicalExamReport> result = reportService.page(page, queryWrapper);
        return Result.success(result);
    }

    /**
     * 高级搜索体检报告
     */
    @GetMapping("/search")
    public Result<IPage<PhysicalExamReport>> search(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String elderName,
            @RequestParam(required = false) String hospital,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Page<PhysicalExamReport> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PhysicalExamReport> queryWrapper = new LambdaQueryWrapper<PhysicalExamReport>()
                .eq(elderId != null, PhysicalExamReport::getElderId, elderId)
                .like(elderName != null && !elderName.isEmpty(), PhysicalExamReport::getElderName, elderName)
                .like(hospital != null && !hospital.isEmpty(), PhysicalExamReport::getHospital, hospital)
                .ge(dateStart != null && !dateStart.isEmpty(), PhysicalExamReport::getDate, dateStart)
                .le(dateEnd != null && !dateEnd.isEmpty(), PhysicalExamReport::getDate, dateEnd)
                .orderByDesc(PhysicalExamReport::getDate);
                
        IPage<PhysicalExamReport> result = reportService.page(page, queryWrapper);
        return Result.success(result);
    }

    /**
     * 编辑体检报告（主表+子表全量覆盖）
     */
    @PutMapping("/update")
    @Transactional
    public Result<?> update(@RequestBody PhysicalExamReportUpdateDTO dto) {
        PhysicalExamReport report = reportService.getById(dto.getId());
        if (report == null) {
            return Result.error("体检报告不存在");
        }
        report.setElderId(dto.getElderId());
        report.setRecorderId(dto.getRecorderId());
        report.setDate(dto.getDate());
        report.setHospital(dto.getHospital());
        report.setMainResult(dto.getMainResult());
        report.setFileUrl(dto.getFileUrl());
        
        // 设置新增字段
        report.setReportNo(dto.getReportNo());
        report.setElderName(dto.getElderName());
        report.setGender(dto.getGender());
        report.setAge(dto.getAge());
        
        reportService.updateById(report);

        // 先删子表，再插入新指标
        itemService.remove(
                new LambdaQueryWrapper<PhysicalExamReportItem>()
                        .eq(PhysicalExamReportItem::getReportId, dto.getId())
        );
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (PhysicalExamReportCreateDTO.PhysicalExamReportItemDTO itemDTO : dto.getItems()) {
                PhysicalExamReportItem item = new PhysicalExamReportItem();
                item.setReportId(dto.getId());
                item.setItemName(itemDTO.getItemName());
                item.setItemValue(itemDTO.getItemValue());
                item.setItemUnit(itemDTO.getItemUnit());
                item.setAbnormalFlag(itemDTO.getAbnormalFlag());
                
                // 设置新增字段
                item.setCategory(itemDTO.getCategory());
                item.setRemark(itemDTO.getRemark());
                item.setNormalRange(itemDTO.getNormalRange());
                
                itemService.save(item);
            }
        }
        return Result.success("更新成功");
    }

    /**
     * 获取体检报告模板
     */
    @GetMapping("/template")
    public Result<String> getTemplate() {
        return Result.success("/templates/physical_exam_template.txt");
    }

    /**
     * 获取所有体检报告分类
     */
    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        return Result.success(PhysicalExamCategories.getAllCategories());
    }

    /**
     * 按分类获取体检项目
     */
    @GetMapping("/items/category/{reportId}/{category}")
    public Result<List<PhysicalExamReportItem>> getItemsByCategory(
            @PathVariable Long reportId,
            @PathVariable String category) {
        try {
            List<PhysicalExamReportItem> items = itemService.getItemsByReportIdAndCategory(reportId, category);
            return Result.success(items);
        } catch (Exception e) {
            return Result.error("获取体检项目分类失败: " + e.getMessage());
        }
    }
} 