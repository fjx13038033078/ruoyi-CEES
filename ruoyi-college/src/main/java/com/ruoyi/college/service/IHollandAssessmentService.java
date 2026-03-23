package com.ruoyi.college.service;

import com.ruoyi.college.domain.HollandQuestion;
import com.ruoyi.college.domain.HollandResult;
import com.ruoyi.college.domain.HollandTestRecord;
import com.ruoyi.college.domain.dto.HollandSubmitDTO;

import java.util.List;

public interface IHollandAssessmentService {

    List<HollandQuestion> getAllActiveQuestions();

    HollandTestRecord startTest(Long userId);

    HollandTestRecord getLatestRecord(Long userId);

    HollandResult submitAnswers(HollandSubmitDTO submitDTO, Long userId);

    HollandResult getResult(Long recordId);

    HollandResult getLatestResult(Long userId);

    List<HollandResult> getResultHistory(Long userId);
}
