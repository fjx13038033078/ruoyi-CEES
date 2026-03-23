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
import java.util.Date;

@TableName("cers_student_profile")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StudentProfile implements Serializable {

    @TableId(value = "profile_id", type = IdType.AUTO)
    private Long profileId;

    private Long userId;

    private String realName;

    private String idCard;

    private String examNumber;

    private String province;

    private Integer examYear;

    /** 科类(1-历史类 2-物理类) */
    private Integer subjectType;

    private String selectedSubjects;

    private Integer totalScore;

    private Integer scoreChinese;

    private Integer scoreMath;

    private Integer scoreEnglish;

    private Integer scorePrimary;

    private Integer scoreSecondary1;

    private Integer scoreSecondary2;

    private String sex;

    private Integer colorBlind;

    private Integer colorWeak;

    /** 认证状态(0待审核 1已通过 2已驳回) */
    private Integer verifyStatus;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
