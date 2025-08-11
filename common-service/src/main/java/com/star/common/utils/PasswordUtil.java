package com.star.common.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码工具类
 * @author star
 */
public class PasswordUtil {

    /**
     * 加密密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 验证密码
     * @param password 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String password, String encodedPassword) {
        return BCrypt.checkpw(password, encodedPassword);
    }

    /**
     * 生成随机密码
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    /**
     * 验证密码强度
     * @param password 密码
     * @return 密码强度等级（1-4）
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 0; // 密码太短
        }

        int score = 0;
        
        // 长度检查
        if (password.length() >= 8) {
            score++;
        }
        
        // 包含小写字母
        if (password.matches(".*[a-z].*")) {
            score++;
        }
        
        // 包含大写字母
        if (password.matches(".*[A-Z].*")) {
            score++;
        }
        
        // 包含数字
        if (password.matches(".*[0-9].*")) {
            score++;
        }
        
        // 包含特殊字符
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            score++;
        }

        return Math.min(score, 4);
    }

    /**
     * 检查密码是否符合基本要求
     * @param password 密码
     * @return 是否符合要求
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            return false;
        }
        
        // 至少包含字母和数字
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        
        return hasLetter && hasDigit;
    }
}