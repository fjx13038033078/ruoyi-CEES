package com.ruoyi.college.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@TableName("cers_holland_result")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class HollandResult implements Serializable {

    @TableId(value = "result_id", type = IdType.AUTO)
    private Long resultId;

    private Long recordId;

    private Long userId;

    @JsonProperty("rScore")
    private Integer rScore;
    @JsonProperty("iScore")
    private Integer iScore;
    @JsonProperty("aScore")
    private Integer aScore;
    @JsonProperty("sScore")
    private Integer sScore;
    @JsonProperty("eScore")
    private Integer eScore;
    @JsonProperty("cScore")
    private Integer cScore;

    private String hollandCode;

    private String personalitySummary;

    private String recommendedMajors;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
