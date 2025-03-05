package com.communitypension.communitypensionadmin.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.HealthRecords;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface HealthRecordsMapper extends BaseMapper<HealthRecords> {

    @Select("SELECT hr.*, e.name AS elder_name, e.phone AS elder_phone, e.address AS elder_address " +
            "FROM health_records hr " +
            "LEFT JOIN elder e ON hr.elder_id = e.id " +
            "WHERE hr.elder_id = #{elderId}")
    HealthRecords selectHealthRecordsWithElderInfoByElderId(@Param("elderId") Long elderId);

    // 更新老人的健康档案
    @Update("UPDATE health_records " +
            "SET blood_pressure = #{healthRecord.bloodPressure}, " +
            "    heart_rate = #{healthRecord.heartRate}, " +
            "    blood_sugar = #{healthRecord.bloodSugar}, " +
            "    temperature = #{healthRecord.temperature}, " +
            "    weight = #{healthRecord.weight}, " +
            "    height = #{healthRecord.height}, " +
            "    symptoms = #{healthRecord.symptoms}, " +
            "    medication = #{healthRecord.medication}, " +
            "    record_time = #{healthRecord.recordTime}, " +
            "    recorder_id = #{healthRecord.recorderId}, " +
            "    record_type = #{healthRecord.recordType}, " +
            "    symptoms_record_time = #{healthRecord.symptomsRecordTime}, " +
            "    elder_id = #{elderId}, "+
            "    updated_at = NOW() " +
            "WHERE id = #{healthRecord.id}")
    int updateHealthRecord(@Param("healthRecord") HealthRecords healthRecord, @Param("elderId") Long elderId);

}