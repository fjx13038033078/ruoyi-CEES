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

@TableName("cers_holland_question")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class HollandQuestion implements Serializable {

    @TableId(value = "question_id", type = IdType.AUTO)
    private Long questionId;

    private String questionText;

    private String optionA;

    private String optionB;

    /** 选项A对应维度(R/I/A/S/E/C) */
    private String dimensionA;

    /** 选项B对应维度(R/I/A/S/E/C) */
    private String dimensionB;

    private Integer sortOrder;

    /** 状态(0启用 1停用) */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
