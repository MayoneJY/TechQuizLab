package com.mayonedev.battle.domain.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiQuestionService {

    private final RestClient restClient;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public AiQuestionService(RestClient.Builder builder) {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60 * 1000);
        factory.setReadTimeout(60 * 1000);

        this.restClient = builder
                .requestFactory(factory)
                .baseUrl("https://gms.ssafy.io/gmsapi/api.openai.com")
                .build();
    }

    public String createInterviewQuestions(String stageContent, String portfolioContent) {

        String systemPrompt = """
                당신은 15년 차 시니어 면접관입니다.
                제공된 채용 공고와 포트폴리오를 분석하여 면접 질문 10개를 생성하세요.
                반드시 아래 JSON 형식으로만 답변하세요. (마크다운 포맷팅 금지)

                [
                    {
                        "question_text": "질문 내용...",
                        "difficulty": "MEDIUM",
                        "tags": "Spring, JPA"
                    }
                ]
                """;

        String userPrompt = String.format("""
                [채용 공고]
                %s

                [지원자 포트폴리오]
                %s
                """, stageContent, portfolioContent);

        return callAi(systemPrompt, userPrompt, "gpt-5-mini");
    }

    public Map<String, Object> evaluateAnswer(String question, String userAnswer) {
        String systemPrompt = """
                당신은 면접관입니다. 지원자의 답변을 평가하세요.
                점수는 0~1000점 사이로 부여하고, 피드백을 한 문장으로 제공하세요.
                반드시 아래 JSON 형식으로만 답변하세요. (마크다운 포맷팅 금지)

                {
                    "score": 850,
                    "feedback": "구체적인 예시가 포함되어 있어 좋습니다."
                }
                """;

        String userPrompt = String.format("""
                [질문]
                %s

                [지원자의 답변]
                %s
                """, question, userAnswer);

        String jsonResponse = callAi(systemPrompt, userPrompt, "gpt-5-mini");

        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(jsonResponse, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("AI 평가 파싱 실패", e);
        }
    }

    private String callAi(String systemPrompt, String userPrompt, String model) {
        // Request Body Creation
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "developer", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)));

        // Execute Request
        Map response = restClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        // Extract Content
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 파싱 실패: " + response, e);
        }
    }
}
