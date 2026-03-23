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

@TableName("cers_volunteer_item")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class VolunteerItem implements Serializable {

    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    private Long formId;

    private Integer sortOrder;

    private Long universityId;

    private Long majorId1;
    private Long majorId2;
    private Long majorId3;
    private Long majorId4;
    private Long majorId5;
    private Long majorId6;

    /** 是否服从专业调剂(0否 1是) */
    private Integer isAdjustment;

    /** 校验状态(0未校验 1通过 2有预警 3不可填报) */
    private Integer checkStatus;

    private String checkMessages;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private String universityName;

    @TableField(exist = false)
    private String majorName1;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
