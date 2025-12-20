package com.mayonedev.battle.domain.payment.controller;

import com.mayonedev.battle.domain.payment.service.PaymentService;
import com.mayonedev.battle.domain.user.dto.UserDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @AuthenticationPrincipal UserDetailsDTO userDetails,
            @RequestBody Map<String, Object> payload) {

        Long userId = userDetails.getUserId();

        String paymentKey = (String) payload.get("paymentKey");
        String orderId = (String) payload.get("orderId");
        int amount = (int) payload.get("amount");
        int quantity = (int) payload.get("quantity"); // We need to send this from frontend

        try {
            paymentService.confirmPayment(paymentKey, orderId, amount, quantity, userId);
            return ResponseEntity.ok().body(Map.of("message", "Payment confirmed", "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", false));
        }
    }
}
