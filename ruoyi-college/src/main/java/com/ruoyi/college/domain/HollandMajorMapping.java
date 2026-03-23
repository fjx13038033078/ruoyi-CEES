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

@TableName("cers_holland_major_mapping")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class HollandMajorMapping implements Serializable {

    @TableId(value = "mapping_id", type = IdType.AUTO)
    private Long mappingId;

    private String hollandCode;

    private String majorDirection;

    private String majorKeywords;

    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
