package com.ruoyi.college.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.college.domain.StudentProfile;
import com.ruoyi.college.domain.VolunteerForm;
import com.ruoyi.college.domain.VolunteerItem;
import com.ruoyi.college.domain.dto.VolunteerCheckResultDTO;
import com.ruoyi.college.mapper.StudentProfileMapper;
import com.ruoyi.college.mapper.VolunteerFormMapper;
import com.ruoyi.college.mapper.VolunteerItemMapper;
import com.ruoyi.university.domain.Major;
import com.ruoyi.university.domain.University;
import com.ruoyi.university.mapper.MajorMapper;
import com.ruoyi.university.mapper.UniversityMapper;
import com.ruoyi.college.service.IVolunteerCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolunteerCheckServiceImpl implements IVolunteerCheckService {

    private final VolunteerFormMapper volunteerFormMapper;
    private final VolunteerItemMapper volunteerItemMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final MajorMapper majorMapper;
    private final UniversityMapper universityMapper;

    private static final Pattern PATTERN_PRIMARY = Pattern.compile("首选(物理|历史)");
    private static final Pattern PATTERN_SECONDARY = Pattern.compile("再选(化学|生物|地理|思想政治|不限)");

    @Override
    public List<VolunteerCheckResultDTO> checkVolunteerForm(Long formId) {
        VolunteerForm form = volunteerFormMapper.selectByFormId(formId);
        if (form == null) {
            return Collections.emptyList();
        }

        StudentProfile profile = studentProfileMapper.selectByUserId(form.getUserId());
        if (profile == null) {
            return Collections.emptyList();
        }

        List<VolunteerItem> items = volunteerItemMapper.selectItemsByFormId(formId);
        List<VolunteerCheckResultDTO> results = new ArrayList<>();
        int totalWarnings = 0;

        Set<Long> seenUniversityIds = new HashSet<>();

        for (VolunteerItem item : items) {
            VolunteerCheckResultDTO dto = new VolunteerCheckResultDTO();
            dto.setItemId(item.getItemId());
            dto.setSortOrder(item.getSortOrder());

            University university = universityMapper.getUniversityById(item.getUniversityId());
            if (university != null) {
                dto.setUniversityName(university.getUniversityName());
            }

            Long[] majorIds = {item.getMajorId1(), item.getMajorId2(), item.getMajorId3(),
                    item.getMajorId4(), item.getMajorId5(), item.getMajorId6()};

            for (Long majorId : majorIds) {
                if (majorId == null) continue;
                Major major = majorMapper.getMajorById(majorId);
                if (major == null) continue;

                if (dto.getMajorName() == null) {
                    dto.setMajorName(major.getMajorName());
                }

                checkScore(profile, major, dto);
                checkSubjectType(profile, major, dto);
                checkSelectedSubjects(profile, major, dto);
                checkSpecialRestrictions(profile, major, dto);
            }

            // 同校重复检测
            if (!seenUniversityIds.add(item.getUniversityId())) {
                dto.addMessage("同一院校出现多次，请注意志愿顺序的合理性");
            }

            // 调剂风险提示
            if (item.getIsAdjustment() != null && item.getIsAdjustment() == 0) {
                dto.addMessage("未选择服从专业调剂，存在退档风险");
            }

            int checkStatus;
            if (dto.getMessages().isEmpty()) {
                checkStatus = 1;
            } else {
                boolean hasBlock = dto.getMessages().stream()
                        .anyMatch(m -> m.contains("不可填报") || m.contains("不符合"));
                checkStatus = hasBlock ? 3 : 2;
                totalWarnings++;
            }
            dto.setCheckStatus(checkStatus);

            volunteerItemMapper.updateCheckResult(item.getItemId(), checkStatus, JSON.toJSONString(dto.getMessages()));
            results.add(dto);
        }

        volunteerFormMapper.updateWarningCount(formId, totalWarnings);

        VolunteerForm updateForm = new VolunteerForm();
        updateForm.setFormId(formId);
        updateForm.setStatus(1);
        volunteerFormMapper.updateForm(updateForm);

        return results;
    }

    private void checkScore(StudentProfile profile, Major major, VolunteerCheckResultDTO dto) {
        if (profile.getTotalScore() == null) return;
        Integer baseline = major.getMinScore2025() != null ? major.getMinScore2025() : major.getMinScore2024();
        if (baseline == null) return;
        int diff = profile.getTotalScore() - baseline;
        if (diff < 0) {
            dto.addMessage(String.format("分数未达投档线（差%d分），不可填报", Math.abs(diff)));
        } else if (diff <= 10) {
            dto.addMessage(String.format("分数仅超投档线%d分，冲刺志愿，录取概率较低", diff));
        } else if (diff > 30) {
            dto.addMessage(String.format("分数超投档线%d分，保底志愿，可考虑更好的院校专业", diff));
        }
    }

    private void checkSubjectType(StudentProfile profile, Major major, VolunteerCheckResultDTO dto) {
        if (profile.getSubjectType() == null || major.getSubject() == null) return;
        if (!profile.getSubjectType().equals(major.getSubject())) {
            String required = major.getSubject() == 1 ? "历史类" : "物理类";
            dto.addMessage(String.format("科类不符合要求（该专业要求%s），不可填报", required));
        }
    }

    private void checkSelectedSubjects(StudentProfile profile, Major major, VolunteerCheckResultDTO dto) {
        if (major.getDescription() == null || profile.getSelectedSubjects() == null) return;
        String desc = major.getDescription();
        String selected = profile.getSelectedSubjects();

        Matcher primaryMatcher = PATTERN_PRIMARY.matcher(desc);
        if (primaryMatcher.find()) {
            String required = primaryMatcher.group(1);
            boolean isPhysics = "物理".equals(required);
            if ((isPhysics && profile.getSubjectType() != null && profile.getSubjectType() != 2)
                    || (!isPhysics && profile.getSubjectType() != null && profile.getSubjectType() != 1)) {
                dto.addMessage(String.format("首选科目不符（要求首选%s）", required));
            }
        }

        Matcher secondaryMatcher = PATTERN_SECONDARY.matcher(desc);
        if (secondaryMatcher.find()) {
            String required = secondaryMatcher.group(1);
            if (!"不限".equals(required) && !selected.contains(required)) {
                dto.addMessage(String.format("再选科目不符（要求再选包含%s）", required));
            }
        }
    }

    private void checkSpecialRestrictions(StudentProfile profile, Major major, VolunteerCheckResultDTO dto) {
        if (major.getDescription() == null) return;
        String desc = major.getDescription();

        if (profile.getColorBlind() != null && profile.getColorBlind() == 1) {
            if (desc.contains("不招色盲") || desc.contains("不招单色识别不全")) {
                dto.addMessage("该专业不招收色盲考生，不可填报");
            }
        }

        if (profile.getColorWeak() != null && profile.getColorWeak() == 1) {
            if (desc.contains("不招色弱") || desc.contains("不招色盲色弱")) {
                dto.addMessage("该专业不招收色弱考生，不可填报");
            }
        }

        if ("1".equals(profile.getSex())) {
            if (desc.contains("只招男生") || desc.contains("请女生慎重")) {
                dto.addMessage("该专业对女生有限制，请慎重填报");
            }
        }
    }
}
