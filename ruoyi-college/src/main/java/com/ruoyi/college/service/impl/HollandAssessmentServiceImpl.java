package com.ruoyi.college.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.college.domain.*;
import com.ruoyi.college.domain.dto.HollandSubmitDTO;
import com.ruoyi.college.mapper.*;
import com.ruoyi.college.service.IHollandAssessmentService;
import com.ruoyi.university.domain.Major;
import com.ruoyi.university.mapper.MajorMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HollandAssessmentServiceImpl implements IHollandAssessmentService {

    private final HollandQuestionMapper questionMapper;
    private final HollandTestRecordMapper recordMapper;
    private final HollandAnswerMapper answerMapper;
    private final HollandResultMapper resultMapper;
    private final HollandMajorMappingMapper mappingMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final MajorMapper majorMapper;

    private static final Map<String, String> DIMENSION_NAME_MAP = new LinkedHashMap<>();
    static {
        DIMENSION_NAME_MAP.put("R", "现实型(Realistic)");
        DIMENSION_NAME_MAP.put("I", "研究型(Investigative)");
        DIMENSION_NAME_MAP.put("A", "艺术型(Artistic)");
        DIMENSION_NAME_MAP.put("S", "社会型(Social)");
        DIMENSION_NAME_MAP.put("E", "企业型(Enterprising)");
        DIMENSION_NAME_MAP.put("C", "常规型(Conventional)");
    }

    private static final Map<String, String> PERSONALITY_MAP = new HashMap<>();
    static {
        PERSONALITY_MAP.put("R", "你喜欢动手操作，倾向于与物体、机器、工具打交道，具有较强的实践能力。");
        PERSONALITY_MAP.put("I", "你善于思考和分析，喜欢探索和研究事物的本质，具有较强的逻辑推理能力。");
        PERSONALITY_MAP.put("A", "你富有创造力和想象力，喜欢通过艺术形式表达自我，追求自由和个性。");
        PERSONALITY_MAP.put("S", "你善于与人交往，乐于助人，具有较强的沟通能力和同理心。");
        PERSONALITY_MAP.put("E", "你具有领导力和决策能力，喜欢组织和管理，善于说服他人。");
        PERSONALITY_MAP.put("C", "你做事认真细致，善于处理数据和信息，遵守规则，注重细节和条理。");
    }

    @Override
    public List<HollandQuestion> getAllActiveQuestions() {
        return questionMapper.selectAllActive();
    }

    @Override
    public HollandTestRecord startTest(Long userId) {
        HollandTestRecord record = new HollandTestRecord();
        record.setUserId(userId);
        record.setStatus(0);
        recordMapper.insertRecord(record);
        return record;
    }

    @Override
    public HollandTestRecord getLatestRecord(Long userId) {
        return recordMapper.selectLatestByUserId(userId);
    }

    @Override
    @Transactional
    public HollandResult submitAnswers(HollandSubmitDTO submitDTO, Long userId) {
        Long recordId = submitDTO.getRecordId();
        HollandTestRecord record = recordMapper.selectByRecordId(recordId);
        if (record == null) {
            throw new RuntimeException("测试记录不存在");
        }

        answerMapper.deleteByRecordId(recordId);

        List<HollandAnswer> answerEntities = new ArrayList<>();
        for (HollandSubmitDTO.AnswerItem item : submitDTO.getAnswers()) {
            HollandAnswer answer = new HollandAnswer();
            answer.setRecordId(recordId);
            answer.setQuestionId(item.getQuestionId());
            answer.setSelectedOption(item.getSelectedOption());
            answerEntities.add(answer);
        }
        if (!answerEntities.isEmpty()) {
            answerMapper.batchInsertAnswers(answerEntities);
        }

        recordMapper.updateStatus(recordId, 1);

        return calculateResult(recordId, userId);
    }

    @Override
    public HollandResult getResult(Long recordId) {
        return resultMapper.selectByRecordId(recordId);
    }

    @Override
    public HollandResult getLatestResult(Long userId) {
        return resultMapper.selectLatestByUserId(userId);
    }

    @Override
    public List<HollandResult> getResultHistory(Long userId) {
        return resultMapper.selectByUserId(userId);
    }

    private HollandResult calculateResult(Long recordId, Long userId) {
        List<HollandAnswer> answers = answerMapper.selectByRecordId(recordId);
        List<HollandQuestion> allQuestions = questionMapper.selectAllActive();
        Map<Long, HollandQuestion> questionMap = allQuestions.stream()
                .collect(Collectors.toMap(HollandQuestion::getQuestionId, q -> q));

        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("R", 0);
        scores.put("I", 0);
        scores.put("A", 0);
        scores.put("S", 0);
        scores.put("E", 0);
        scores.put("C", 0);

        for (HollandAnswer answer : answers) {
            HollandQuestion question = questionMap.get(answer.getQuestionId());
            if (question == null) continue;

            String dimension;
            if ("A".equals(answer.getSelectedOption())) {
                dimension = question.getDimensionA();
            } else {
                dimension = question.getDimensionB();
            }
            scores.merge(dimension, 1, Integer::sum);
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String hollandCode = sorted.stream().limit(3).map(Map.Entry::getKey).collect(Collectors.joining());

        StringBuilder personality = new StringBuilder();
        personality.append("你的霍兰德代码为 ").append(hollandCode).append("。\n\n");
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            String dim = sorted.get(i).getKey();
            personality.append("【").append(DIMENSION_NAME_MAP.get(dim)).append("】");
            personality.append(PERSONALITY_MAP.getOrDefault(dim, "")).append("\n");
        }

        List<HollandMajorMapping> mappings = findMappings(hollandCode);
        List<Map<String, Object>> recommendedList = buildRecommendations(mappings, userId);

        HollandResult result = new HollandResult();
        result.setRecordId(recordId);
        result.setUserId(userId);
        result.setRScore(scores.get("R"));
        result.setIScore(scores.get("I"));
        result.setAScore(scores.get("A"));
        result.setSScore(scores.get("S"));
        result.setEScore(scores.get("E"));
        result.setCScore(scores.get("C"));
        result.setHollandCode(hollandCode);
        result.setPersonalitySummary(personality.toString());
        result.setRecommendedMajors(JSON.toJSONString(recommendedList));

        resultMapper.insertResult(result);
        return result;
    }

    private List<HollandMajorMapping> findMappings(String hollandCode) {
        Set<HollandMajorMapping> allMappings = new LinkedHashSet<>();

        List<HollandMajorMapping> exactMatch = mappingMapper.selectByHollandCode(hollandCode);
        allMappings.addAll(exactMatch);

        for (int i = 0; i < hollandCode.length(); i++) {
            String singleDim = String.valueOf(hollandCode.charAt(i));
            allMappings.addAll(mappingMapper.selectByHollandCode(singleDim));
        }

        if (hollandCode.length() >= 2) {
            String twoChar = hollandCode.substring(0, 2);
            allMappings.addAll(mappingMapper.selectByHollandCode(twoChar));
        }

        return new ArrayList<>(allMappings);
    }

    private List<Map<String, Object>> buildRecommendations(List<HollandMajorMapping> mappings, Long userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        StudentProfile profile = studentProfileMapper.selectByUserId(userId);

        for (HollandMajorMapping mapping : mappings) {
            Map<String, Object> direction = new HashMap<>();
            direction.put("direction", mapping.getMajorDirection());
            direction.put("description", mapping.getDescription());
            direction.put("hollandCode", mapping.getHollandCode());

            List<Map<String, Object>> matchedMajors = new ArrayList<>();
            if (mapping.getMajorKeywords() != null) {
                String[] keywords = mapping.getMajorKeywords().split(",");
                for (String keyword : keywords) {
                    Major query = new Major();
                    query.setKeyword(keyword.trim());
                    if (profile != null && profile.getSubjectType() != null) {
                        query.setSubject(profile.getSubjectType());
                    }
                    List<Major> majors = majorMapper.getAllMajors(query);
                    for (Major m : majors) {
                        Integer baseline = m.getMinScore2025() != null ? m.getMinScore2025() : m.getMinScore2024();
                        boolean scoreOk = true;
                        if (profile != null && profile.getTotalScore() != null && baseline != null) {
                            scoreOk = profile.getTotalScore() >= baseline - 20;
                        }
                        if (scoreOk && matchedMajors.size() < 5) {
                            Map<String, Object> mInfo = new HashMap<>();
                            mInfo.put("majorId", m.getMajorId());
                            mInfo.put("majorName", m.getMajorName());
                            mInfo.put("universityId", m.getUniversityId());
                            mInfo.put("universityName", m.getUniversityName());
                            mInfo.put("minScore2024", m.getMinScore2024());
                            mInfo.put("minScore2025", m.getMinScore2025());
                            matchedMajors.add(mInfo);
                        }
                    }
                }
            }
            direction.put("majors", matchedMajors);
            result.add(direction);
        }
        return result;
    }
}
