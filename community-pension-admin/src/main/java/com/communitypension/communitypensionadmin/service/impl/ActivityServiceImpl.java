package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.constant.DictTypeConstants;
import com.communitypension.communitypensionadmin.pojo.dto.ActivityDTO;
import com.communitypension.communitypensionadmin.pojo.dto.ActivityQuery;
import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.ActivityMapper;
import com.communitypension.communitypensionadmin.service.ActivityService;
import com.communitypension.communitypensionadmin.service.DictDataService;
import com.communitypension.communitypensionadmin.pojo.vo.ActivityVO;
import com.communitypension.communitypensionadmin.pojo.vo.DictDataVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 活动Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final DictDataService dictDataService;

    @Override
    public Page<ActivityVO> getActivityList(ActivityQuery query) {
        Page<Activity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        wrapper.like(StringUtils.hasText(query.getTitle()), Activity::getTitle, query.getTitle())
                .eq(query.getStatus() != null, Activity::getStatus, query.getStatus())
                .eq(StringUtils.hasText(query.getType()), Activity::getType, query.getType())
                .ge(query.getStartTime() != null, Activity::getStartTime, query.getStartTime())
                .le(query.getEndTime() != null, Activity::getEndTime, query.getEndTime())
                .orderByDesc(Activity::getCreatedAt);

        // 执行分页查询
        Page<Activity> activityPage = page(page, wrapper);

        // 转换为VO
        List<ActivityVO> voList = activityPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<ActivityVO> resultPage = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    private ActivityVO convertToVO(Activity activity) {
        ActivityVO vo = new ActivityVO();
        BeanUtils.copyProperties(activity, vo);

        // 设置活动类型名称
        if (StringUtils.hasText(activity.getType())) {
            DictDataVO dictDatavVO = dictDataService.getDictDataByTypeAndValue("activity_type", activity.getType());
            if (dictDatavVO != null) {
                vo.setTypeName(dictDatavVO.getDictLabel());
            } else {
                vo.setTypeName("");
            }
        } else {
            vo.setTypeName("未设置类型");
        }

        // 设置状态名称
        vo.setStatusName(
            com.communitypension.communitypensionadmin.utils.DictUtils.getDictLabel(
                com.communitypension.communitypensionadmin.constant.DictTypeConstants.ACTIVITY_STATUS,
                activity.getStatus() == null ? null : String.valueOf(activity.getStatus())
            )
        );

        // 设置当前参与人数
        vo.setCurrentParticipants(baseMapper.getCurrentRegisters(activity.getId()));

        return vo;
    }

    /**
     * 获取活动详情
     */
    @Override
    public ActivityVO getActivityDetail(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        return convertToVO(activity);
    }

    /**
     * 创建活动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(ActivityDTO dto) {
        // 验证活动时间
        validateActivityTime(dto.getStartTime(), dto.getEndTime());

        Activity activity = new Activity();
        BeanUtils.copyProperties(dto, activity);
        activity.setStatus(0); // 默认为筹备中状态

        save(activity);
    }

    /**
     * 更新活动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(Long id, ActivityDTO dto) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        
        // 获取当前活动状态
        Integer currentStatus = activity.getStatus();
        
        // 对于进行中或已结束的活动，不验证开始时间是否早于当前时间
        boolean skipStartTimeValidation = currentStatus != null && (currentStatus == 2 || currentStatus == 3);

        // 验证活动时间
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
    }

    /**
     * 更新活动状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityStatus(Long id, Integer status) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 验证状态转换是否合法
        validateStatusTransition(activity.getStatus(), status);

        activity.setStatus(status);
        updateById(activity);
    }

    /**
     * 验证状态转换是否合法
     *
     * @param currentStatus 当前状态
     * @param newStatus     新状态
     */
    private void validateStatusTransition(Integer currentStatus, Integer newStatus) {
        // 如果要转换为已取消状态，且当前不是进行中状态，则允许转换
        if (newStatus == 4 && currentStatus != 2) {
            return;
        }
        
        // 定义其他状态转换规则
        Map<Integer, Set<Integer>> allowedTransitions = new HashMap<>();
        allowedTransitions.put(0, Set.of(1, 4)); // 筹备中 -> 报名中/已取消
        allowedTransitions.put(1, Set.of(2, 4)); // 报名中 -> 进行中/已取消
        allowedTransitions.put(2, Set.of(3, 4)); // 进行中 -> 已结束/已取消
        allowedTransitions.put(3, Set.of(4));    // 已结束 -> 已取消
        allowedTransitions.put(4, Set.of());     // 已取消 -> 无法再转换

        Set<Integer> allowedStatuses = allowedTransitions.get(currentStatus);
        if (allowedStatuses == null || !allowedStatuses.contains(newStatus)) {
            throw new BusinessException("非法的状态转换");
        }
    }

    /**
     * 删除活动
     *
     * @param id 活动ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 只限制进行中的活动不能删除
        if (activity.getStatus() == 2) {
            // 获取状态名称
            String statusName = com.communitypension.communitypensionadmin.utils.DictUtils.getDictLabel(
                com.communitypension.communitypensionadmin.constant.DictTypeConstants.ACTIVITY_STATUS,
                "2"
            );
            
            log.warn("尝试删除进行中的活动，活动ID: {}, 标题: {}", 
                    activity.getId(), activity.getTitle());
            
            throw new BusinessException("进行中的活动不能删除");
        }

        removeById(id);
    }

    /**
     * 获取活动统计信息
     */
    @Override
    public Map<String, Object> getActivityStats(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalParticipants", baseMapper.getCurrentRegisters(id));
        stats.put("maxParticipants", activity.getMaxParticipants());
        stats.put("remainingSlots", activity.getMaxParticipants() - baseMapper.getCurrentRegisters(id));

        return stats;
    }

    /**
     * 导出活动列表
     */
    @Override
    public void exportActivityList(String title, Integer status, String startTime, String endTime, HttpServletResponse response) {
        // 查询活动列表
        ActivityQuery query = new ActivityQuery();
        query.setTitle(title);
        query.setStatus(status);
        // 这里假设startTime和endTime是字符串，需要转换为LocalDateTime，如果不是请直接传LocalDateTime
        // query.setStartTimeRange(startTime);
        // query.setEndTimeRange(endTime);
        // 改为：
        // 你可以根据实际需要解析字符串为LocalDateTime
        // 这里简单处理，实际项目建议用DateTimeFormatter解析
        // query.setStartTime(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // query.setEndTime(LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // 如果startTime和endTime为null则不设置
        query.setPageNum(1);
        query.setPageSize(Integer.MAX_VALUE);
        List<ActivityVO> activityList = getActivityList(query).getRecords();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("活动列表");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"活动ID", "活动标题", "活动类型", "活动地点", "开始时间", "结束时间", "最大参与人数", "当前参与人数", "活动状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据行
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < activityList.size(); i++) {
                ActivityVO activity = activityList.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(activity.getId());
                row.createCell(1).setCellValue(activity.getTitle());
                row.createCell(2).setCellValue(activity.getTypeName());
                row.createCell(3).setCellValue(activity.getLocation());
                row.createCell(4).setCellValue(activity.getStartTime() != null ? activity.getStartTime().format(formatter) : "");
                row.createCell(5).setCellValue(activity.getEndTime() != null ? activity.getEndTime().format(formatter) : "");
                row.createCell(6).setCellValue(activity.getMaxParticipants() != null ? activity.getMaxParticipants() : 0);
                Integer participants = baseMapper.getCurrentRegisters(activity.getId());
                row.createCell(7).setCellValue(participants != null ? participants : 0);
                row.createCell(8).setCellValue(activity.getStatusName());
                row.createCell(9).setCellValue(activity.getCreatedAt() != null ? activity.getCreatedAt().format(formatter) : "");
            }

            // 设置响应头和内容类型
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=activity_list.xlsx");

            // 将工作簿写入响应输出流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出活动列表失败", e);
        }
    }

    /**
     * 获取进行中的活动数量
     */
    @Override
    public Long getOngoingActivityCount() {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 2); // 状态为进行中
        return count(wrapper);
    }

    /**
     * 根据指定时间获取进行中的活动数量
     */
    @Override
    public Long getOngoingActivityCountByTime(LocalDateTime time) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 2) // 状态为进行中
                .le(Activity::getStartTime, time) // 开始时间小于等于指定时间
                .ge(Activity::getEndTime, time);  // 结束时间大于等于指定时间
        return count(wrapper);
    }

    /**
     * 获取指定时间之前创建的活动数量
     */
    @Override
    public Long getCountBeforeTime(LocalDateTime time) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Activity::getCreatedAt, time);
        return count(wrapper);
    }

    /**
     * 获取活动类型分布数据
     */
    @Override
    public Map<String, Long> getActivityCountByType() {
        // 获取所有活动
        List<Activity> activities = list();

        // 获取活动类型字典数据
        List<DictDataVO> dictList = dictDataService.getDictDataByType(DictTypeConstants.ACTIVITY_TYPE);
        Map<String, String> dictMap = dictList.stream()
                .collect(Collectors.toMap(DictDataVO::getDictValue, DictDataVO::getDictLabel));

        // 按活动类型分组并计数
        Map<String, Long> typeCounts = activities.stream()
                .collect(Collectors.groupingBy(
                        activity -> dictMap.getOrDefault(activity.getType(), "其他"),
                        Collectors.counting()
                ));

        return typeCounts;
    }

    /**
     * 获取最近的活动
     */
    @Override
    public List<Map<String, Object>> getRecentActivities(Integer limit) {
        // 获取最近创建的活动，按创建时间降序排列
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Activity::getCreatedAt);
        Page<Activity> page = new Page<>(1, limit);
        Page<Activity> activityPage = page(page, wrapper);

        // 获取活动状态和类型字典数据
        List<DictDataVO> statusDictList = dictDataService.getDictDataByType(DictTypeConstants.ACTIVITY_STATUS);
        Map<String, String> statusMap = statusDictList.stream()
                .collect(Collectors.toMap(DictDataVO::getDictValue, DictDataVO::getDictLabel));

        List<DictDataVO> typeDictList = dictDataService.getDictDataByType(DictTypeConstants.ACTIVITY_TYPE);
        Map<String, String> typeMap = typeDictList.stream()
                .collect(Collectors.toMap(DictDataVO::getDictValue, DictDataVO::getDictLabel));

        // 转换为前端需要的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> result = activityPage.getRecords().stream().map(activity -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", activity.getId());
            map.put("name", activity.getTitle());
            map.put("date", activity.getStartTime() != null ? activity.getStartTime().format(formatter) : "");
            map.put("participants", baseMapper.getCurrentRegisters(activity.getId()));
            map.put("status", statusMap.getOrDefault(activity.getStatus() != null ? activity.getStatus().toString() : "", "未知"));
            map.put("type", typeMap.getOrDefault(activity.getType(), "其他"));
            map.put("location", activity.getLocation());
            map.put("maxParticipants", activity.getMaxParticipants() != null ? activity.getMaxParticipants() : 0);
            return map;
        }).collect(Collectors.toList());

        return result;
    }
}