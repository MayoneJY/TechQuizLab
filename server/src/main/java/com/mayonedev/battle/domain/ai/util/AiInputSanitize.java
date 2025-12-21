package com.mayonedev.battle.domain.ai.util;

import org.springframework.stereotype.Component;
//
// 사용자의 프롬프트 악용을 무력화.
//
@Component
public class AiInputSanitize {

    public String sanitize(String input) {
        if (input == null) return "";

        String s = input;

        int MAX_LEN = 8000; //길이 제한
        if (s.length() > MAX_LEN) s = s.substring(0, MAX_LEN);

        s = s.replaceAll("(?i)ignore\\s+previous\\s+instructions", "");
        s = s.replaceAll("(?i)follow\\s+these\\s+instructions", "");
        s = s.replaceAll("(?i)system\\s*prompt", "");
        s = s.replaceAll("(?i)developer\\s*message", "");
        s = s.replaceAll("(?i)you\\s+are\\s+(chatgpt|an\\s+ai|a\\s+language\\s+model)", "");
        s = s.replaceAll("(?i)role\\s*:", "");
        s = s.replaceAll("(?i)###", "");

        s = s.replaceAll("\\p{Cntrl}", " ");
        s = s.replaceAll("\\s+", " ").trim();

        return s;
    }
}
