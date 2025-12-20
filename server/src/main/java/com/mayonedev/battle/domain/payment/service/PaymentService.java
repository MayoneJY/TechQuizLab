package com.mayonedev.battle.domain.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper; // Unused
import com.mayonedev.battle.domain.payment.dao.PaymentDao;
import com.mayonedev.battle.domain.payment.entity.Payment;
import com.mayonedev.battle.domain.user.dao.UserDao;
import com.mayonedev.battle.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentDao paymentDao;
    private final UserDao userDao;
    // private final ObjectMapper objectMapper; // Unused
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${toss.payment.secret-key:test_sk_Z0RnYX2w53255kl21R2r8NeyqApQ}") // Default to a random test key
                                                                              // format/placeholder if not set
    private String tossSecretKey;

    public int calculateAmount(int quantity) {
        if (quantity >= 10)
            return quantity * 2600;
        if (quantity >= 5)
            return quantity * 2700;
        return quantity * 3000;
    }

    @Transactional
    public void confirmPayment(String paymentKey, String orderId, int amount, int quantity, Long userId) {
        // 1. Verify Price
        int expectedAmount = calculateAmount(quantity);
        if (amount != expectedAmount) {
            throw new RuntimeException("Price mismatch. Expected: " + expectedAmount + ", Got: " + amount);
        }

        // 2. Call Toss Payments Confirm API
        // In real impl, use the secret key from properties.
        // For this demo, using the one requested or a test key.
        // Note: The user said "use test key". I'll use the proper test key if I can
        // find one or just assume config.
        // I will use a placeholder test key or assume it is in properties for security.
        // "test_sk_Z0RnYX2w53255kl21R2r8NeyqApQ" is just a placeholder example.
        // Actually, for "test_sk_..." usually it works in test env.
        // Let's rely on @Value or hardcoded for now if user didn't provide one.
        // I'll put a REAL looking test key placeholder, but user might need to change
        // it.
        // Actually, let's just proceed with verification logic.

        HttpHeaders headers = new HttpHeaders();
        String secretKey = tossSecretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    request,
                    JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // 3. Save Payment
                Payment payment = Payment.builder()
                        .paymentKey(paymentKey)
                        .orderId(orderId)
                        .userId(userId)
                        .amount(amount)
                        .quantity(quantity)
                        .status("DONE")
                        .requestedAt(LocalDateTime.now()) // Approximation
                        .approvedAt(LocalDateTime.now())
                        .build();
                paymentDao.insert(payment);

                // 4. Charge Lives
                User user = userDao.findById(userId);
                if (user != null) {
                    if (user.getRemainingLives() == null)
                        user.setRemainingLives(0);
                    user.setRemainingLives(user.getRemainingLives() + quantity);
                    userDao.update(user);
                }
            } else {
                throw new RuntimeException("Payment confirmation failed: " + response.getStatusCode());
            }

        } catch (Exception e) {
            // Log error
            e.printStackTrace();
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }
}
