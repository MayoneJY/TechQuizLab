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

//        int MAX_LEN = 8000; //길이 제한
//        if (s.length() > MAX_LEN) s = s.substring(0, MAX_LEN);

        s = s.replaceAll("(?i)ignore\\s+previous\\s+instructions", "");
        s = s.replaceAll("(?i)follow\\s+these\\s+instructions", "");
        s = s.replaceAll("(?i)system\\s*prompt", "");
        s = s.replaceAll("(?i)developer\\s*message", "");
        s = s.replaceAll("(?i)you\\s+are\\s+(chatgpt|an\\s+ai|a\\s+language\\s+model)", "");
        s = s.replaceAll("(?i)give\\s+me\\s+\\d+\\s*points?", "");
        s = s.replaceAll("(?i)give\\s+full\\s+score", "");
        s = s.replaceAll("(?i)max\\s+score", "");

        s = s.replaceAll("(?i)role\\s*:", "");
        s = s.replaceAll("(?i)###", "");
        
        s = s.replaceAll("(?i)난이도\\s*(를|을)?\\s*(바꿔|변경|조정)(줘|해줘|주세요)?", "");
        s = s.replaceAll("(?i)(easy|eazy|ez)\\s*로\\s*(해줘|바꿔|조정).*", "");
        s = s.replaceAll("(?i)(hard|hurd)\\s*로\\s*(해줘|바꿔|조정).*", "");
        s = s.replaceAll("(?i)(medium|mid|middle)\\s*로\\s*(해줘|바꿔|조정).*", "");
        s = s.replaceAll("(?i)규칙\\s*(바꿔|변경|무시)(줘|해줘|주세요)?", "");
        s = s.replaceAll("(?i)프롬프트\\s*(바꿔|지워|무시)(줘|해줘|주세요)?", "");

        s = s.replaceAll("(?i)(점수|1000점|천점|만점)\\s*(를|을)?\\s*(줘|주세요|주셈|부여|처리|올려|높게)", "");
        s = s.replaceAll("(?i)(전부|모든|남은)\\s*(문제|질문)?\\s*(점수|1000점|만점)\\s*(를|을)?\\s*(줘|주세요|부여|처리)", "");
        s = s.replaceAll("(?i)무조건\\s*(점수|1000점|만점)\\s*(줘|부여|처리)", "");


        s = s.replaceAll("\\p{Cntrl}", " ");
        s = s.replaceAll("\\s+", " ").trim();

        return s;
    }
}
