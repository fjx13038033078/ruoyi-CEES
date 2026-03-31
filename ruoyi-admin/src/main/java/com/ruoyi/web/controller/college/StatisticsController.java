package com.ruoyi.web.controller.college;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.university.domain.Major;
import com.ruoyi.university.domain.Province;
import com.ruoyi.university.domain.University;
import com.ruoyi.university.service.MajorService;
import com.ruoyi.university.service.ProvinceService;
import com.ruoyi.university.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/college/statistics")
public class StatisticsController extends BaseController {

    private final UniversityService universityService;
    private final MajorService majorService;
    private final ProvinceService provinceService;

    /**
     * 院校下拉列表（id + name，不分页）
     */
    @GetMapping("/universityOptions")
    public AjaxResult universityOptions() {
        List<University> all = universityService.getAllUniversities(null);
        List<Map<String, Object>> options = all.stream().map(u -> {
            Map<String, Object> m = new HashMap<>(2);
            m.put("universityId", u.getUniversityId());
            m.put("universityName", u.getUniversityName());
            return m;
        }).collect(Collectors.toList());
        return success(options);
    }

    /**
     * 指定院校的专业投档线走势（2023-2025）
     */
    @GetMapping("/majorTrend")
    public AjaxResult majorTrend(@RequestParam Long universityId) {
        List<Major> majors = majorService.getMajorsByUniversityId(universityId);
        return success(majors);
    }

    /**
     * 所有省份数据（不分页，用于投档线走势和高校资源对比）
     */
    @GetMapping("/provinces")
    public AjaxResult allProvinces() {
        List<Province> all = provinceService.getAllProvinces();
        return success(all);
    }

    /**
     * 高校层次与类型分布统计
     */
    @GetMapping("/universityDist")
    public AjaxResult universityDistribution() {
        List<University> all = universityService.getAllUniversities(null);

        int total = all.size();
        int count985 = 0, count211 = 0, countDoubleFirst = 0;
        int countPublic = 0, countPrivate = 0;
        int countBachelor = 0, countCollege = 0;

        for (University u : all) {
            if (Integer.valueOf(1).equals(u.getIs985())) count985++;
            if (Integer.valueOf(1).equals(u.getIs211())) count211++;
            if (Integer.valueOf(1).equals(u.getIsDoubleFirst())) countDoubleFirst++;
            if (Integer.valueOf(0).equals(u.getType())) countPublic++;
            if (Integer.valueOf(1).equals(u.getType())) countPrivate++;
            if (Integer.valueOf(0).equals(u.getLevel())) countBachelor++;
            if (Integer.valueOf(1).equals(u.getLevel())) countCollege++;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("count985", count985);
        result.put("count211Only", count211 - count985);
        result.put("countDoubleFirstOnly", Math.max(0, countDoubleFirst - count211));
        result.put("countNormal", total - countDoubleFirst);
        result.put("countPublic", countPublic);
        result.put("countPrivate", countPrivate);
        result.put("countBachelor", countBachelor);
        result.put("countCollege", countCollege);
        return success(result);
    }
}
