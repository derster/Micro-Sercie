package com.derster.paymentService.service;


import com.derster.paymentService.model.PaymentRequest;
import com.derster.paymentService.model.PaymentResponse;

import java.util.List;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
