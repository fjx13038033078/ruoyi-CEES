package com.ruoyi.college.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HollandSubmitDTO implements Serializable {

    private Long recordId;

    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        /** A 或 B */
        private String selectedOption;
    }
}
