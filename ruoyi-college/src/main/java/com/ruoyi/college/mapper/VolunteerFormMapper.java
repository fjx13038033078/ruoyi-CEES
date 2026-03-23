package com.ruoyi.college.mapper;

import com.ruoyi.college.domain.VolunteerForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VolunteerFormMapper {

    List<VolunteerForm> selectFormListByUserId(Long userId);

    VolunteerForm selectByFormId(Long formId);

    int insertForm(VolunteerForm form);

    int updateForm(VolunteerForm form);

    int updateWarningCount(@Param("formId") Long formId, @Param("warningCount") Integer warningCount);

    int deleteByFormId(Long formId);
}
