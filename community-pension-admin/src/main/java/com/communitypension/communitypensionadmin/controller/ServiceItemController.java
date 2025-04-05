package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.dto.ServiceItemDTO;
import com.communitypension.communitypensionadmin.entity.ServiceItem;
import com.communitypension.communitypensionadmin.service.ServiceItemService;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.vo.ServiceItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务项目Controller
 */
@Tag(name = "服务项目管理")
@RestController
@RequestMapping("/api/service/item")
@RequiredArgsConstructor
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @Operation(summary = "分页查询服务项目列表")
    @GetMapping("/list")
    public Result<Page<ServiceItemVO>> list(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size, ServiceItem serviceItem) {
        Page<ServiceItem> page = new Page<>(current, size);
        Page<ServiceItemVO> pageVO = serviceItemService.getServiceItemList(page, serviceItem);
        return Result.success(pageVO);
    }

    @Operation(summary = "获取服务项目详情")
    @GetMapping("/{serviceId}")
    public Result<ServiceItemVO> getInfo(@PathVariable Long serviceId) {
        return Result.success(serviceItemService.getServiceItemById(serviceId));
    }

    @Operation(summary = "新增服务项目")
    @PostMapping
    public Result<Boolean> add(@Validated @RequestBody ServiceItemDTO serviceItemDTO) {
        return Result.success(serviceItemService.addServiceItem(serviceItemDTO));
    }

    @Operation(summary = "修改服务项目")
    @PutMapping
    public Result<Boolean> edit(@Validated @RequestBody ServiceItemDTO serviceItemDTO) {
        return Result.success(serviceItemService.updateServiceItem(serviceItemDTO));
    }

    @Operation(summary = "删除服务项目")
    @DeleteMapping("/{serviceId}")
    public Result<Boolean> remove(@PathVariable Long serviceId) {
        return Result.success(serviceItemService.deleteServiceItem(serviceId));
    }

    @Operation(summary = "批量删除服务项目")
    @DeleteMapping("/batch/{serviceIds}")
    public Result<Boolean> batchRemove(@PathVariable List<Long> serviceIds) {
        return Result.success(serviceItemService.batchDeleteServiceItems(serviceIds));
    }


    @Operation(summary = "更新服务项目状态")
    @PutMapping("/{status}/{serviceId}")
    public Result<Boolean> updateStatus(@PathVariable String status, @PathVariable Long serviceId) {
        return Result.success(serviceItemService.updateServiceItemStatus(serviceId, status));
    }
} 