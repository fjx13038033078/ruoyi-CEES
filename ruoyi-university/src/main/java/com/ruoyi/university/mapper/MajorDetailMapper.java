package com.ruoyi.university.mapper;

import com.ruoyi.university.domain.MajorDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MajorDetailMapper {

    /**
     * 根据专业组ID查询下辖的具体专业明细
     *
     * @param majorId 专业组ID（cers_major.major_id）
     * @return 专业明细列表
     */
    List<MajorDetail> selectByMajorId(Long majorId);
}
