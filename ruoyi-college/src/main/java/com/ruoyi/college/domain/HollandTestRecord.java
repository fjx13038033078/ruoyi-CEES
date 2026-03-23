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

@TableName("cers_holland_test_record")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class HollandTestRecord implements Serializable {

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    private Long userId;

    /** 状态(0进行中 1已完成) */
    private Integer status;

    private Date startTime;

    private Date finishTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
