package com.communitypension.communitypensionadmin.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.HealthRecords;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface HealthRecordsMapper extends BaseMapper<HealthRecords> {

// 使用@Select注解来定义一个SQL查询语句
    @Select("SELECT hr.*, e.name AS elder_name " +  // 选择health_records表中的所有字段，并将elder表中的name字段重命名为elder_name
            "FROM health_records hr " +  // 从health_records表中选择数据
            "LEFT JOIN user e ON hr.elder_id = e.user_id " +  // 使用LEFT JOIN将health_records表与elder表连接，连接条件是elder_id等于elder表的主键id
            "WHERE hr.elder_id = #{elderId}")  // 查询条件是health_records表中的elder_id等于传入的elderId参数
// 定义一个接口方法selectHealthRecordsWithElderInfoByElderId，用于根据elderId查询健康记录，并包含老人信息
    HealthRecords selectHealthRecordsWithElderInfoByElderId(@Param("elderId") Long elderId);  // @Param注解用于将方法参数elderId绑定到SQL查询中的#{elderId}占位符

    // 更新老人的健康档案
    @Update("UPDATE health_records " + // 使用@Update注解指定SQL更新语句，操作health_records表
            "SET blood_pressure = #{healthRecord.bloodPressure}, " + // 设置血压字段为传入的healthRecord对象的bloodPressure属性值
            "    heart_rate = #{healthRecord.heartRate}, " + // 设置心率字段为传入的healthRecord对象的heartRate属性值
            "    blood_sugar = #{healthRecord.bloodSugar}, " + // 设置血糖字段为传入的healthRecord对象的bloodSugar属性值
            "    temperature = #{healthRecord.temperature}, " + // 设置体温字段为传入的healthRecord对象的temperature属性值
            "    weight = #{healthRecord.weight}, " + // 设置体重字段为传入的healthRecord对象的weight属性值
            "    height = #{healthRecord.height}, " + // 设置身高字段为传入的healthRecord对象的height属性值
            "    symptoms = #{healthRecord.symptoms}, " + // 设置症状字段为传入的healthRecord对象的symptoms属性值
            "    medication = #{healthRecord.medication}, " + // 设置药物字段为传入的healthRecord对象的medication属性值
            "    record_time = #{healthRecord.recordTime}, " + // 设置记录时间为传入的healthRecord对象的recordTime属性值
            "    recorder_id = #{healthRecord.recorderId}, " + // 设置记录者ID字段为传入的healthRecord对象的recorderId属性值
            "    record_type = #{healthRecord.recordType}, " + // 设置记录类型字段为传入的healthRecord对象的recordType属性值
            "    symptoms_record_time = #{healthRecord.symptomsRecordTime}, " + // 设置症状记录时间为传入的healthRecord对象的symptomsRecordTime属性值
            "    elder_id = #{elderId}, "+ // 设置老人ID字段为传入的elderId参数值
            "    updated_at = NOW() " + // 设置更新时间为当前时间
            "WHERE id = #{healthRecord.id}") // 指定更新条件为传入的healthRecord对象的id属性值
    int updateHealthRecord(@Param("healthRecord") HealthRecords healthRecord, @Param("elderId") Long elderId); // 定义方法updateHealthRecord，接收两个参数：healthRecord对象和elderId，返回更新操作影响的行数

}