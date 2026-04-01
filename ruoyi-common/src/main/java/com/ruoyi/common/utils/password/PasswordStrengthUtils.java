package com.ruoyi.common.utils.password;

import com.ruoyi.common.utils.StringUtils;

/**
 * 密码强度评估：与注册页「低 / 中 / 高」展示规则一致。
 */
public final class PasswordStrengthUtils {

    /** 注册允许的最低强度（至少为「中」） */
    public static final PasswordStrength MIN_REGISTER_STRENGTH = PasswordStrength.MEDIUM;

    private PasswordStrengthUtils() {
    }

    /**
     * 评估密码强度。
     * <ul>
     *   <li>低：长度不足 8，或仅单一字符类型</li>
     *   <li>中：长度≥8，且至少包含字母、数字、特殊字符中的两类（大小写合并为「字母」）</li>
     *   <li>高：长度≥10，且同时包含大写、小写、数字、特殊字符中的至少三类（四类更佳）</li>
     * </ul>
     */
    public static PasswordStrength evaluate(String password) {
        if (StringUtils.isEmpty(password)) {
            return PasswordStrength.LOW;
        }
        int len = password.length();
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit((char) ch));

        int categories = 0;
        if (hasLower || hasUpper) {
            categories++;
        }
        if (hasDigit) {
            categories++;
        }
        if (hasSpecial) {
            categories++;
        }

        if (len >= 10 && categories >= 3) {
            return PasswordStrength.HIGH;
        }
        if (len >= 8 && categories >= 2) {
            return PasswordStrength.MEDIUM;
        }
        return PasswordStrength.LOW;
    }

    public static boolean isAcceptableForRegister(String password) {
        return evaluate(password).ordinal() >= MIN_REGISTER_STRENGTH.ordinal();
    }
}
