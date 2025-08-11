package com.star.common.utils;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * @author star
 */
public class ValidateUtil {

    /**
     * 邮箱正则表达式
     */
    private static final String EMAIL_PATTERN = 
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    /**
     * 手机号正则表达式
     */
    private static final String MOBILE_PATTERN = "^1[3-9]\\d{9}$";

    /**
     * 身份证号正则表达式
     */
    private static final String ID_CARD_PATTERN = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

    /**
     * URL正则表达式
     */
    private static final String URL_PATTERN = 
            "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    /**
     * IPv4正则表达式
     */
    private static final String IPV4_PATTERN = 
            "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern mobilePattern = Pattern.compile(MOBILE_PATTERN);
    private static final Pattern idCardPattern = Pattern.compile(ID_CARD_PATTERN);
    private static final Pattern urlPattern = Pattern.compile(URL_PATTERN);
    private static final Pattern ipv4Pattern = Pattern.compile(IPV4_PATTERN);

    /**
     * 验证邮箱格式
     * @param email 邮箱
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    /**
     * 验证手机号格式
     * @param mobile 手机号
     * @return 是否有效
     */
    public static boolean isValidMobile(String mobile) {
        return mobile != null && mobilePattern.matcher(mobile).matches();
    }

    /**
     * 验证身份证号格式
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }
        
        if (!idCardPattern.matcher(idCard).matches()) {
            return false;
        }
        
        // 验证校验码
        return validateIdCardChecksum(idCard);
    }

    /**
     * 验证身份证号校验码
     * @param idCard 身份证号
     * @return 是否有效
     */
    private static boolean validateIdCardChecksum(String idCard) {
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Character.getNumericValue(idCard.charAt(i)) * weights[i];
        }
        
        int mod = sum % 11;
        char expectedCheckCode = checkCodes[mod];
        char actualCheckCode = Character.toUpperCase(idCard.charAt(17));
        
        return expectedCheckCode == actualCheckCode;
    }

    /**
     * 验证URL格式
     * @param url URL
     * @return 是否有效
     */
    public static boolean isValidUrl(String url) {
        return url != null && urlPattern.matcher(url).matches();
    }

    /**
     * 验证IPv4地址格式
     * @param ip IP地址
     * @return 是否有效
     */
    public static boolean isValidIPv4(String ip) {
        return ip != null && ipv4Pattern.matcher(ip).matches();
    }

    /**
     * 验证字符串是否为数字
     * @param str 字符串
     * @return 是否为数字
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证字符串是否为整数
     * @param str 字符串
     * @return 是否为整数
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证字符串长度是否在指定范围内
     * @param str 字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是否在范围内
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength <= 0;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 验证字符串是否只包含字母
     * @param str 字符串
     * @return 是否只包含字母
     */
    public static boolean isAlpha(String str) {
        return str != null && str.matches("^[a-zA-Z]+$");
    }

    /**
     * 验证字符串是否只包含字母和数字
     * @param str 字符串
     * @return 是否只包含字母和数字
     */
    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * 验证字符串是否只包含中文
     * @param str 字符串
     * @return 是否只包含中文
     */
    public static boolean isChinese(String str) {
        return str != null && str.matches("^[\\u4e00-\\u9fa5]+$");
    }

    /**
     * 验证用户名格式（字母、数字、下划线，4-20位）
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{4,20}$");
    }

    /**
     * 验证昵称格式（中文、字母、数字，2-10位）
     * @param nickname 昵称
     * @return 是否有效
     */
    public static boolean isValidNickname(String nickname) {
        return nickname != null && nickname.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9]{2,10}$");
    }

    /**
     * 验证QQ号格式
     * @param qq QQ号
     * @return 是否有效
     */
    public static boolean isValidQQ(String qq) {
        return qq != null && qq.matches("^[1-9][0-9]{4,11}$");
    }

    /**
     * 验证微信号格式
     * @param wechat 微信号
     * @return 是否有效
     */
    public static boolean isValidWechat(String wechat) {
        return wechat != null && wechat.matches("^[a-zA-Z][-_a-zA-Z0-9]{5,19}$");
    }

    /**
     * 验证银行卡号格式
     * @param bankCard 银行卡号
     * @return 是否有效
     */
    public static boolean isValidBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 16 || bankCard.length() > 19) {
            return false;
        }
        return bankCard.matches("^[0-9]+$");
    }

    /**
     * 验证邮政编码格式
     * @param zipCode 邮政编码
     * @return 是否有效
     */
    public static boolean isValidZipCode(String zipCode) {
        return zipCode != null && zipCode.matches("^[1-9]\\d{5}$");
    }
}