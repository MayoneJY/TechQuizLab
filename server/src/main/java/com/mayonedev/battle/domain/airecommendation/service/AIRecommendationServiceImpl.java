package com.mayonedev.battle.domain.airecommendation.service;

import com.mayonedev.battle.domain.airecommendation.constants.AIRecommendationConstants;
import com.mayonedev.battle.domain.airecommendation.dto.*;
import com.mayonedev.battle.exception.AIRecommendationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIRecommendationServiceImpl implements AIRecommendationService {

    private final RestTemplate restTemplate;

    @Value("${ai.recommendation.url:http://localhost:8000}")
    private String aiRecommendationBaseUrl;

    @Override
    public LearningPlanResponse getLearningPlan(Long userId, Integer limit) {
        String endpoint = AIRecommendationConstants.LEARNING_PLAN_ENDPOINT.replace("{userId}", String.valueOf(userId));
        Map<String, Object> params = new HashMap<>();
        if (limit != null) {
            params.put("limit", limit);
        }
        
        String url = buildUrl(endpoint, params);
        
        return executeApiCall(
                url,
                HttpMethod.GET,
                LearningPlanResponse.class,
                "학습 계획",
                userId,
                limit
        );
    }

    @Override
    public StageRecommendationResponse getStageRecommendations(Long userId, Integer limit, Integer stageLimit) {
        String endpoint = AIRecommendationConstants.STAGE_RECOMMENDATIONS_ENDPOINT.replace("{userId}", String.valueOf(userId));
        Map<String, Object> params = new HashMap<>();
        if (limit != null) {
            params.put("limit", limit);
        }
        if (stageLimit != null) {
            params.put("stageLimit", stageLimit);
        }
        
        String url = buildUrl(endpoint, params);
        
        return executeApiCall(
                url,
                HttpMethod.GET,
                StageRecommendationResponse.class,
                "스테이지 추천",
                userId,
                limit,
                stageLimit
        );
    }

    private String buildUrl(String endpoint, Map<String, Object> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(aiRecommendationBaseUrl + endpoint);
        
        params.forEach((key, value) -> {
            if (value != null) {
                builder.queryParam(key, value);
            }
        });
        
        return builder.toUriString();
    }

    private <T> T executeApiCall(String url, HttpMethod method, Class<T> responseType, 
                               String operationName, Object... logParams) {
        try {
            log.info("{} API 호출 - url: {}", operationName, url);
            ResponseEntity<T> response = restTemplate.exchange(url, method, null, responseType);
            
            T body = response.getBody();
            if (body == null) {
                log.warn("{} API 응답이 null입니다. url: {}", operationName, url);
                throw new AIRecommendationException(
                        HttpStatus.NO_CONTENT,
                        operationName + " 데이터가 없습니다."
                );
            }
            
            return body;
        } catch (ResourceAccessException e) {
            // 연결 실패, 타임아웃 등 네트워크 관련 오류
            Throwable rootCause = e.getRootCause();
            String errorMessage;
            
            if (rootCause instanceof SocketTimeoutException) {
                if (rootCause.getMessage() != null && rootCause.getMessage().contains("Read timed out")) {
                    errorMessage = String.format(
                        "%s API 호출 시 읽기 타임아웃이 발생했습니다. (URL: %s, 원인: %s)",
                        operationName, url, rootCause.getMessage()
                    );
                } else {
                    errorMessage = String.format(
                        "%s API 호출 시 연결 타임아웃이 발생했습니다. (URL: %s, 원인: %s)",
                        operationName, url, rootCause.getMessage()
                    );
                }
            } else if (rootCause instanceof ConnectException) {
                errorMessage = String.format(
                    "%s API 서비스에 연결할 수 없습니다. 서비스가 실행 중인지 확인해주세요. (URL: %s, 원인: %s)",
                    operationName, url, rootCause.getMessage()
                );
            } else {
                errorMessage = String.format(
                    "%s API 호출 시 네트워크 오류가 발생했습니다. (URL: %s, 원인: %s)",
                    operationName, url, rootCause != null ? rootCause.getMessage() : e.getMessage()
                );
            }
            
            log.error("{} API 호출 실패 [네트워크 오류] - url: {}", operationName, url, e);
            throw new AIRecommendationException(errorMessage, e);
            
        } catch (HttpClientErrorException e) {
            // 4xx 클라이언트 오류
            String errorMessage = String.format(
                "%s API 호출 시 클라이언트 오류가 발생했습니다. (URL: %s, HTTP 상태: %s, 응답: %s)",
                operationName, url, e.getStatusCode(), e.getResponseBodyAsString()
            );
            log.error("{} API 호출 실패 [클라이언트 오류] - url: {}, status: {}", 
                     operationName, url, e.getStatusCode(), e);
            throw new AIRecommendationException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    errorMessage
            );
            
        } catch (HttpServerErrorException e) {
            // 5xx 서버 오류
            String errorMessage = String.format(
                "%s API 서버에서 오류가 발생했습니다. (URL: %s, HTTP 상태: %s, 응답: %s)",
                operationName, url, e.getStatusCode(), e.getResponseBodyAsString()
            );
            log.error("{} API 호출 실패 [서버 오류] - url: {}, status: {}", 
                     operationName, url, e.getStatusCode(), e);
            throw new AIRecommendationException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    errorMessage
            );
            
        } catch (AIRecommendationException e) {
            throw e;
            
        } catch (RestClientException e) {
            // 기타 RestClientException
            String errorMessage = String.format(
                "%s API 호출 시 예상치 못한 오류가 발생했습니다. (URL: %s, 원인: %s)",
                operationName, url, e.getMessage()
            );
            log.error("{} API 호출 실패 [기타 RestClientException] - url: {}", operationName, url, e);
            throw new AIRecommendationException(errorMessage, e);
            
        } catch (Exception e) {
            String errorMessage = String.format(
                "%s 조회 중 예상치 못한 오류가 발생했습니다. (URL: %s, 원인: %s)",
                operationName, url, e.getMessage()
            );
            log.error("예상치 못한 오류 발생 - {} API 호출, url: {}", operationName, url, e);
            throw new AIRecommendationException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    errorMessage
            );
        }
    }
}

