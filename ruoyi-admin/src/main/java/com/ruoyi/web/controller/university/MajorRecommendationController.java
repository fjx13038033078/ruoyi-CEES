package com.ruoyi.web.controller.university;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.university.domain.Major;
import com.ruoyi.university.domain.Province;
import com.ruoyi.university.service.MajorRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import static com.ruoyi.common.utils.PageUtils.startPage;

/**
 * @Author 范佳兴
 * @date 2025/2/21 15:48
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/university/recommendation")
public class MajorRecommendationController extends BaseController {

    private final MajorRecommendationService majorRecommendationService;

    /**
     * 获取推荐专业列表
     * @return
     */
    @GetMapping("/listAll")
    public AjaxResult recommendMajors() {
        List<Major> majors = majorRecommendationService.recommendMajors();
        return success(majors);
    }

    /**
     * 冲刺/稳妥/保底 三梯度推荐
     */
    @GetMapping("/tiered")
    public AjaxResult tieredRecommendations(@RequestParam(defaultValue = "10") Integer limit) {
        java.util.Map<String, Object> result = majorRecommendationService.getTieredRecommendations(limit);
        if (result == null || result.isEmpty()) {
            return error("请先完善个人信息（学科类型和高考成绩）");
        }
        return success(result);
    }
}
