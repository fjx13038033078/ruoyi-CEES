package com.ruoyi.university.service.impl;

import com.ruoyi.university.domain.MajorDetail;
import com.ruoyi.university.mapper.MajorDetailMapper;
import com.ruoyi.university.service.MajorDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MajorDetailServiceImpl implements MajorDetailService {

    private final MajorDetailMapper majorDetailMapper;

    @Override
    public List<MajorDetail> getDetailsByMajorId(Long majorId) {
        return majorDetailMapper.selectByMajorId(majorId);
    }
}
