package com.ruoyi.college.mapper;

import com.ruoyi.college.domain.HollandTestRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HollandTestRecordMapper {

    List<HollandTestRecord> selectByUserId(Long userId);

    HollandTestRecord selectByRecordId(Long recordId);

    HollandTestRecord selectLatestByUserId(Long userId);

    int insertRecord(HollandTestRecord record);

    int updateStatus(@Param("recordId") Long recordId, @Param("status") Integer status);
}
