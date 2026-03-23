package com.ruoyi.college.service;

import com.ruoyi.college.domain.StudentProfile;

import java.util.List;

public interface IStudentProfileService {

    List<StudentProfile> selectProfileList(StudentProfile query);

    StudentProfile selectByUserId(Long userId);

    StudentProfile selectByProfileId(Long profileId);

    int createProfile(StudentProfile profile);

    int updateProfile(StudentProfile profile);

    int updateVerifyStatus(Long profileId, Integer verifyStatus);

    int deleteByProfileId(Long profileId);

    boolean checkIdCardUnique(String idCard, Long profileId);

    boolean checkExamNumberUnique(String examNumber, Long profileId);
}
