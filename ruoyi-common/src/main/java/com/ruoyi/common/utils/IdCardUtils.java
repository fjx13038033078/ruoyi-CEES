package com.ruoyi.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 中国大陆居民身份证号码校验（18 位校验码、15 位老证格式与日期合理性）
 */
public final class IdCardUtils {

    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] CHECK_CHARS = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    private IdCardUtils() {
    }

    /**
     * 是否通过格式与校验码（及日期）校验
     */
    public static boolean isValid(String idCard) {
        if (StringUtils.isEmpty(idCard)) {
            return false;
        }
        String s = idCard.trim().toUpperCase();
        if (s.length() == 18) {
            return validate18(s);
        }
        if (s.length() == 15) {
            return validate15(s);
        }
        return false;
    }

    private static boolean validate18(String s) {
        if (!s.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9X]$")) {
            return false;
        }
        String birth = s.substring(6, 14);
        if (!isValidBirthDate(birth)) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (s.charAt(i) - '0') * WEIGHTS[i];
        }
        int mod = sum % 11;
        char expected = CHECK_CHARS[mod];
        return s.charAt(17) == expected;
    }

    private static boolean validate15(String s) {
        if (!s.matches("^[1-9]\\d{7}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$")) {
            return false;
        }
        String birth = "19" + s.substring(6, 12);
        return isValidBirthDate(birth);
    }

    private static boolean isValidBirthDate(String yyyyMMdd) {
        try {
            LocalDate d = LocalDate.parse(yyyyMMdd, DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate min = LocalDate.of(1900, 1, 1);
            LocalDate max = LocalDate.now().plusDays(1);
            return !d.isBefore(min) && !d.isAfter(max);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
