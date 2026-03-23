package com.ruoyi.web.controller.college;

import com.ruoyi.college.domain.HollandQuestion;
import com.ruoyi.college.domain.HollandResult;
import com.ruoyi.college.domain.HollandTestRecord;
import com.ruoyi.college.domain.dto.HollandSubmitDTO;
import com.ruoyi.college.service.IHollandAssessmentService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/college/assessment")
public class HollandAssessmentController extends BaseController {

    private final IHollandAssessmentService hollandAssessmentService;

    @GetMapping("/questions")
    public AjaxResult getQuestions() {
        List<HollandQuestion> list = hollandAssessmentService.getAllActiveQuestions();
        return success(list);
    }

    @PostMapping("/start")
    public AjaxResult startTest() {
        Long userId = SecurityUtils.getUserId();
        HollandTestRecord record = hollandAssessmentService.startTest(userId);
        return success(record);
    }

    @GetMapping("/latestRecord")
    public AjaxResult getLatestRecord() {
        Long userId = SecurityUtils.getUserId();
        HollandTestRecord record = hollandAssessmentService.getLatestRecord(userId);
        return success(record);
    }

    @PostMapping("/submit")
    public AjaxResult submitAnswers(@RequestBody HollandSubmitDTO submitDTO) {
        Long userId = SecurityUtils.getUserId();
        HollandResult result = hollandAssessmentService.submitAnswers(submitDTO, userId);
        return success(result);
    }

    @GetMapping("/result/{recordId}")
    public AjaxResult getResult(@PathVariable Long recordId) {
        HollandResult result = hollandAssessmentService.getResult(recordId);
        return success(result);
    }

    @GetMapping("/latestResult")
    public AjaxResult getLatestResult() {
        Long userId = SecurityUtils.getUserId();
        HollandResult result = hollandAssessmentService.getLatestResult(userId);
        return success(result);
    }

    @GetMapping("/resultHistory")
    public AjaxResult getResultHistory() {
        Long userId = SecurityUtils.getUserId();
        List<HollandResult> list = hollandAssessmentService.getResultHistory(userId);
        return success(list);
    }
}
