package com.ruoyi.college.mapper;

import com.ruoyi.college.domain.HollandQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HollandQuestionMapper {

    List<HollandQuestion> selectAllActive();

    HollandQuestion selectByQuestionId(Long questionId);

    List<HollandQuestion> selectList(HollandQuestion query);

    int insertQuestion(HollandQuestion question);

    int updateQuestion(HollandQuestion question);

    int deleteByQuestionId(Long questionId);
}
