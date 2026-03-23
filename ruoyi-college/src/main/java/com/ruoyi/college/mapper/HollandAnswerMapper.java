package com.ruoyi.college.mapper;

import com.ruoyi.college.domain.HollandAnswer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HollandAnswerMapper {

    List<HollandAnswer> selectByRecordId(Long recordId);

    int insertAnswer(HollandAnswer answer);

    int batchInsertAnswers(List<HollandAnswer> answers);

    int deleteByRecordId(Long recordId);
}
