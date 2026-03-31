package com.ruoyi.university.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author 范佳兴
 * @date 2025/2/20 17:13
 */
@TableName("cers_major")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Major implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long majorId;

    private Long universityId;

    @TableField(exist = false)
    private String universityName;

    /** 专业编号 */
    private Integer majorNum;

    /** 专业组名称 */
    private String majorName;

    /** 报名科目（1-历史类，2-物理类） */
    private Integer subject;

    /** 备注 */
    private String description;

    /** 2023年投档线 */
    private Integer minScore2023;

    /** 2024年投档线 */
    private Integer minScore2024;

    /** 2025年投档线 */
    private Integer minScore2025;

    @TableField(exist = false)
    private String keyword;

    @TableField(exist = false)
    private int minScore;

    @TableField(exist = false)
    private int maxScore;

    /** 列表筛选/排序所用投档线年份：2023、2024、2025；不传则按 COALESCE(2025,2024,2023) 筛选与排序 */
    @TableField(exist = false)
    private Integer scoreYear;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
