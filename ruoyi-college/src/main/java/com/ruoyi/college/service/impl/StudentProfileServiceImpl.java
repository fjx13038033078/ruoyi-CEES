package com.ruoyi.college.service.impl;

import com.ruoyi.college.domain.StudentProfile;
import com.ruoyi.college.mapper.StudentProfileMapper;
import com.ruoyi.college.service.IStudentProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl implements IStudentProfileService {

    private final StudentProfileMapper studentProfileMapper;

    @Override
    public List<StudentProfile> selectProfileList(StudentProfile query) {
        return studentProfileMapper.selectProfileList(query);
    }

    @Override
    public StudentProfile selectByUserId(Long userId) {
        return studentProfileMapper.selectByUserId(userId);
    }

    @Override
    public StudentProfile selectByProfileId(Long profileId) {
        return studentProfileMapper.selectByProfileId(profileId);
    }

    @Override
    public StudentProfile selectByIdCard(String idCard) {
        return studentProfileMapper.selectByIdCard(idCard);
    }

    @Override
    public int createProfile(StudentProfile profile) {
        if (profile.getVerifyStatus() == null) {
            profile.setVerifyStatus(0);
        }
        return studentProfileMapper.insertProfile(profile);
    }

    @Override
    public int updateProfile(StudentProfile profile) {
        return studentProfileMapper.updateProfile(profile);
    }

    @Override
    public int updateVerifyStatus(Long profileId, Integer verifyStatus) {
        return studentProfileMapper.updateVerifyStatus(profileId, verifyStatus);
    }

    @Override
    public int deleteByProfileId(Long profileId) {
        return studentProfileMapper.deleteByProfileId(profileId);
    }

    @Override
    public boolean checkIdCardUnique(String idCard, Long profileId) {
        StudentProfile existing = studentProfileMapper.selectByIdCard(idCard);
        if (existing == null) {
            return true;
        }
        return existing.getProfileId().equals(profileId);
    }

    @Override
    public boolean checkExamNumberUnique(String examNumber, Long profileId) {
        StudentProfile existing = studentProfileMapper.selectByExamNumber(examNumber);
        if (existing == null) {
            return true;
        }
        return existing.getProfileId().equals(profileId);
    }
}
