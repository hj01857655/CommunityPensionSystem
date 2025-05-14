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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        reportService.save(report);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (PhysicalExamReportCreateDTO.PhysicalExamReportItemDTO itemDTO : dto.getItems()) {
                PhysicalExamReportItem item = new PhysicalExamReportItem();
                item.setReportId(report.getId());
                item.setItemName(itemDTO.getItemName());
                item.setItemValue(itemDTO.getItemValue());
                item.setItemUnit(itemDTO.getItemUnit());
                item.setAbnormalFlag(itemDTO.getAbnormalFlag());
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
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<PhysicalExamReport> page = new Page<>(pageNum, pageSize);
        IPage<PhysicalExamReport> result = reportService.lambdaQuery()
                .eq(PhysicalExamReport::getElderId, elderId)
                .orderByDesc(PhysicalExamReport::getDate)
                .page(page);
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
} 