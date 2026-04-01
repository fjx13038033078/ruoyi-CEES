package com.ruoyi.common.utils.password;

/**
 * 密码安全性等级：低 / 中 / 高
 */
public enum PasswordStrength {
    /** 不符合注册要求 */
    LOW,
    /** 可注册：长度与字符类型达到中等要求 */
    MEDIUM,
    /** 强密码 */
    HIGH
}
