package com.ruoyi.college.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class VolunteerCheckResultDTO implements Serializable {

    private Long itemId;

    private Integer sortOrder;

    private String universityName;

    private String majorName;

    /** 校验状态(1通过 2有预警 3不可填报) */
    private Integer checkStatus;

    private List<String> messages = new ArrayList<>();

    public void addMessage(String msg) {
        this.messages.add(msg);
    }
}
