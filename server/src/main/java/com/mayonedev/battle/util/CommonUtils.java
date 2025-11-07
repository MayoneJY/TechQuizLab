package com.mayonedev.battle.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class CommonUtils {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 이메일 형식 유효성 검사
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 문자열이 null 또는 비어있는지 확인
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * LocalDateTime을 문자열로 포맷팅
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }
    
    /**
     * 현재 시간을 문자열로 반환
     */
    public static String getCurrentTimeString() {
        return formatDateTime(LocalDateTime.now());
    }
    
    /**
     * 사용자명 유효성 검사 (3-20자, 영문+숫자+언더스코어만)
     */
    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) {
            return false;
        }
        return username.length() >= 3 && username.length() <= 20 
            && username.matches("^[a-zA-Z0-9_]+$");
    }
    
    /**
     * 비밀번호 유효성 검사 (최소 6자)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}