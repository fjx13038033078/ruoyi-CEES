package com.ruoyi.college.service;

import com.ruoyi.college.domain.dto.VolunteerCheckResultDTO;

import java.util.List;

public interface IVolunteerCheckService {

    List<VolunteerCheckResultDTO> checkVolunteerForm(Long formId);
}
