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
import java.util.List;

@TableName("cers_volunteer_form")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class VolunteerForm implements Serializable {

    @TableId(value = "form_id", type = IdType.AUTO)
    private Long formId;

    private Long userId;

    private String formName;

    private Integer examYear;

    /** 批次类型(1提前批 2本科批 3专科批) */
    private Integer batchType;

    /** 状态(0草稿 1已校验 2已锁定) */
    private Integer status;

    private Integer totalItems;

    private Integer warningCount;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private List<VolunteerItem> items;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
