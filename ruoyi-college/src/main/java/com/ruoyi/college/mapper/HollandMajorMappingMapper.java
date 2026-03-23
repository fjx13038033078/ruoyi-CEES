package com.ruoyi.college.mapper;

import com.ruoyi.college.domain.HollandMajorMapping;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HollandMajorMappingMapper {

    List<HollandMajorMapping> selectByHollandCode(String hollandCode);

    List<HollandMajorMapping> selectAll();

    HollandMajorMapping selectByMappingId(Long mappingId);

    int insertMapping(HollandMajorMapping mapping);

    int updateMapping(HollandMajorMapping mapping);

    int deleteByMappingId(Long mappingId);
}
