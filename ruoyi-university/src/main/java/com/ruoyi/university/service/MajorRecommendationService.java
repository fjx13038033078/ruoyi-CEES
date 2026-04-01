package com.ruoyi.university.service;

import com.ruoyi.university.domain.Major;

import java.util.List;
import java.util.Map;

/**
 * @Author 范佳兴
 * @date 2025/2/21 15:13
 */
public interface MajorRecommendationService {
    /**
     * 根据用户行为记录，生成推荐的专业列表
     *
     * @return 推荐的专业列表
     */
    List<Major> recommendMajors();

    List<Major> getCombinedRecommendations();

    /**
     * 按冲刺/稳妥/保底三梯度推荐，每梯度最多 limit 个，附带录取概率
     */
    Map<String, Object> getTieredRecommendations(int limit);
}
