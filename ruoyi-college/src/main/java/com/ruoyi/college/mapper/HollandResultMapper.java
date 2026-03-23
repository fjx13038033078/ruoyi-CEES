package com.ruoyi.college.mapper;

import com.ruoyi.college.domain.HollandResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HollandResultMapper {

    HollandResult selectByRecordId(Long recordId);

    HollandResult selectLatestByUserId(Long userId);

    List<HollandResult> selectByUserId(Long userId);

    int insertResult(HollandResult result);
}
