package com.ruoyi.college.mapper;

import com.ruoyi.college.domain.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentProfileMapper {

    List<StudentProfile> selectProfileList(StudentProfile query);

    StudentProfile selectByUserId(Long userId);

    StudentProfile selectByProfileId(Long profileId);

    StudentProfile selectByIdCard(String idCard);

    StudentProfile selectByExamNumber(String examNumber);

    int insertProfile(StudentProfile profile);

    int updateProfile(StudentProfile profile);

    int updateVerifyStatus(@Param("profileId") Long profileId, @Param("verifyStatus") Integer verifyStatus);

    int deleteByProfileId(Long profileId);
}
