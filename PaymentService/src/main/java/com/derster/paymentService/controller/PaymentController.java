package com.derster.paymentService.controller;

import com.derster.paymentService.model.PaymentRequest;
import com.derster.paymentService.model.PaymentResponse;
import com.derster.paymentService.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService productService;

    public PaymentController(PaymentService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
        return new ResponseEntity<>(
                productService.doPayment(paymentRequest), HttpStatus.OK
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable long orderId){
        return new ResponseEntity<>(
                productService.getPaymentDetailsByOrderId(orderId), HttpStatus.OK
        );
    }

}
