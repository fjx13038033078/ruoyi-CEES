package com.ruoyi.university.service;

import com.ruoyi.university.domain.MajorDetail;

import java.util.List;

public interface MajorDetailService {

    /**
     * 根据专业组ID查询下辖的具体专业明细
     *
     * @param majorId 专业组ID
     * @return 专业明细列表
     */
    List<MajorDetail> getDetailsByMajorId(Long majorId);
}
