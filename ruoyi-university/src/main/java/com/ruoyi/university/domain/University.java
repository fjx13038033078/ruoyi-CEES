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
 * @date 2025/2/6 9:44
 */
@TableName("cers_university")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class University implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long universityId;

    private String universityName;

    private String location;

    /** 所在城市 */
    private String city;

    /** 高校类型（0-公办，1-民办） */
    private Integer type;

    private Integer is985;

    private Integer is211;

    /** 是否双一流（0-否，1-是） */
    private Integer isDoubleFirst;

    /** 高校层次（0-本科，1-专科） */
    private Integer level;

    /** 院校官网 */
    private String webUrl;

    @TableField(exist = false)
    private String keyword;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
