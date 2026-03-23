package com.ruoyi.college.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@TableName("cers_holland_answer")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class HollandAnswer implements Serializable {

    @TableId(value = "answer_id", type = IdType.AUTO)
    private Long answerId;

    private Long recordId;

    private Long questionId;

    /** 选择的选项(A/B) */
    private String selectedOption;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
