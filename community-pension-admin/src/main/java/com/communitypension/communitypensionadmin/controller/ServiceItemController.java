package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.common.Result;
import com.communitypension.communitypensionadmin.dto.ServiceItemDTO;
import com.communitypension.communitypensionadmin.entity.ServiceItem;
import com.communitypension.communitypensionadmin.service.ServiceItemService;
import com.communitypension.communitypensionadmin.vo.ServiceItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 服务项目Controller
 */
@Api(tags = "服务项目管理")
@RestController
@RequestMapping("/service/item")
@RequiredArgsConstructor
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @ApiOperation("分页查询服务项目列表")
    @GetMapping("/list")
    public Result<Page<ServiceItemVO>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            ServiceItem serviceItem) {
        Page<ServiceItem> page = new Page<>(current, size);
        return Result.success(serviceItemService.getServiceItemList(page, serviceItem));
    }

    @ApiOperation("获取服务项目详情")
    @GetMapping("/{serviceId}")
    public Result<ServiceItemVO> getInfo(@PathVariable Long serviceId) {
        return Result.success(serviceItemService.getServiceItemById(serviceId));
    }

    @ApiOperation("新增服务项目")
    @PostMapping
    public Result<Boolean> add(@Validated @RequestBody ServiceItemDTO serviceItemDTO) {
        return Result.success(serviceItemService.addServiceItem(serviceItemDTO));
    }

    @ApiOperation("修改服务项目")
    @PutMapping
    public Result<Boolean> edit(@Validated @RequestBody ServiceItemDTO serviceItemDTO) {
        return Result.success(serviceItemService.updateServiceItem(serviceItemDTO));
    }

    @ApiOperation("删除服务项目")
    @DeleteMapping("/{serviceId}")
    public Result<Boolean> remove(@PathVariable Long serviceId) {
        return Result.success(serviceItemService.deleteServiceItem(serviceId));
    }
} 