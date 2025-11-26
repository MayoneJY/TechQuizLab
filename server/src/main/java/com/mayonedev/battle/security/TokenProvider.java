package com.mayonedev.battle.security;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.mayonedev.battle.dto.UserDetailsDTO;
import com.mayonedev.battle.entity.User;

import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 생성하고 검증하는 Provider
 */
@Component
@Slf4j
public class TokenProvider {
    // private static final long ACCESS_TOKEN_VALID_PERIOD = 1000L * 60 * 60 * 24 *
    // 7; // 7일
    // @Value("${jwt.secret-key}")
    private static SecretKey jwtSecretKey;

    public TokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        TokenProvider.jwtSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * '토큰의 만료기간'을 생성하는 메서드
     * 
     * @param isAccessToken : AccessToken 인지 여부
     * @return {Date} : 만료일
     */
    private static Date createExpirationDate(boolean isAccessToken) {
        Calendar c = Calendar.getInstance();
        if (isAccessToken) {
            c.add(Calendar.HOUR, 1);
        } else {
            c.add(Calendar.DATE, 7);
        }
        return c.getTime();
    }

    /**
     * 토큰의 Header를 생성하는 메서드
     * 
     * @return {Map<String, Object>} : 토큰의 Header
     */
    private static Map<String, Object> createHeader() {
        return Jwts.header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .add("regDate", System.currentTimeMillis())
                .build();
    }

    /**
     * 토큰의 Claims를 생성하는 메서드
     * 
     * @param user : 사용자 정보
     * @return {Map<String, Object>} : 토큰의 Claims
     */
    private static Map<String, Object> createClaims(UserDetailsDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        return claims;
    }

    /**
     * 토큰의 유효성을 검사하는 메서드
     * 
     * @param token : 검사할 토큰
     * @return {ValidToken} : 토큰의 유효성
     */
    public static ValidToken isValidToken(String token) {
        try {
            // Claims claims = getTokenToClaims(token);
            return ValidToken.builder().isValid(true).errorName(null).build();
        } catch (ExpiredJwtException e) {
            return ValidToken.builder().isValid(false).errorName("TOKEN_EXPIRED").build();
        } catch (JwtException e) {
            return ValidToken.builder().isValid(false).errorName("TOKEN_INVALID").build();
        } catch (NullPointerException e) {
            return ValidToken.builder().isValid(false).errorName("TOKEN_NULL").build();
        }
    }

    /**
     * JWT 토큰을 생성하는 메서드
     * 
     * @param user          : 사용자 정보
     * @param isAccessToken : AccessToken 인지 여부
     * @return {String} : 생성된 JWT 토큰
     */
    public static String generateJWT(UserDetailsDTO user, boolean isAccessToken) {
        return Jwts.builder()
                .header().empty().add(createHeader()).and()
                .claims().empty().add(createClaims(user)).and()
                .subject(String.valueOf(user.getId()))
                .signWith(jwtSecretKey)
                .expiration(createExpirationDate(isAccessToken))
                .compact();
    }

    /**
     * 헤더에서 토큰 정보를 추출하는 메서드
     * 
     * @param header : 헤더
     * @return {String} : 토큰 정보
     */
    public static String getHeaderToToken(String header) {
        return header.split(" ")[1];
    }

    /**
     * 토큰을 Claims로 변환하는 메서드
     * 
     * @param token : 토큰
     * @return {Claims} : Claims
     */
    public static Claims getTokenToClaims(String token) {
        return Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token).getPayload();
    }

    public static String getClaimsToUserEmail(String token) {
        return getTokenToClaims(token).get("email").toString();
    }
}
