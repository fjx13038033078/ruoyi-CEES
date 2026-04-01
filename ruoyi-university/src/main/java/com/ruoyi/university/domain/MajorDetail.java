package com.ruoyi.university.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@TableName("cers_major_detail")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class MajorDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;

    private Long majorId;

    /** 具体专业名称（如：软件工程、临床医学） */
    private String specificMajorName;

    /** 招生计划人数 */
    private Integer planCount;

    /** 学费（元/年） */
    private Integer tuitionFee;

    /** 学制（如：4年、5年） */
    private String studyYears;

    /** 特殊要求及备注 */
    private String requirements;
}
