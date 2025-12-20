package com.mayonedev.battle.domain.payment.dao;

import com.mayonedev.battle.domain.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentDao {
    int insert(Payment payment);

    Payment findByPaymentKey(String paymentKey);
}
