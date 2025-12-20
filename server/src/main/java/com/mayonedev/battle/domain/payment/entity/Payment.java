package com.mayonedev.battle.domain.payment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String paymentKey; // Toss Payment Key (PK)
    private String orderId; // Order ID
    private Long userId; // FK to User
    private Integer amount; // Payment Amount
    private Integer quantity; // Number of lives purchased
    private String status; // DONE, CANCELED, ABORTED
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
}
