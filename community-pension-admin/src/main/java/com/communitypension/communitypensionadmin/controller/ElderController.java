package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.Elder;
import com.communitypension.communitypensionadmin.service.ElderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/elders")
@CrossOrigin(origins = "http://localhost:8081")
public class ElderController {
    private static final Logger logger = Logger.getLogger(ElderController.class.getName());
    @Autowired
    private final ElderService elderService;

    public ElderController(ElderService elderService) {
        this.elderService = elderService;
    }

    /**
     * 获取所有老人信息
     */
    @GetMapping
    public ResponseEntity<List<Elder>> getAllElders() {
        List<Elder> elderList=elderService.list();
        //日志框架打印日志
        elderList.forEach(elder -> logger.info(elder.toString()));
        return ResponseEntity.ok(elderList);
    }


    @GetMapping("/{id}")
    public Elder getElderById(@PathVariable Long id) {
        return elderService.getById(id);
    }

    @PostMapping("")
    public void addElder(@RequestBody Elder elder) {
        elderService.save(elder);
    }

    /**
     * 更新老人信息
     */
    @PutMapping("")
    public void updateElder(@RequestBody Elder elder) {
        elderService.updateById(elder);
    }
    /**
     * 删除老人信息
     */
    @DeleteMapping("{id}")
    public void deleteElder(@PathVariable Long id) {
        elderService.removeById(id);
    }


}
