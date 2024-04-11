package com.derster.oderService.service;

import com.derster.oderService.entity.Order;
import com.derster.oderService.exception.CustomException;
import com.derster.oderService.external.client.PaymentService;
import com.derster.oderService.external.client.ProductService;
import com.derster.oderService.external.request.PaymentRequest;
import com.derster.oderService.external.response.PaymentResponse;
import com.derster.oderService.model.OrderRequest;
import com.derster.oderService.model.OrderResponse;
import com.derster.oderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.UUID;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService, PaymentService paymentService, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.paymentService = paymentService;
        this.restTemplate = restTemplate;
    }

    @Override
    public long pladeOrder(OrderRequest orderRequest) {

        log.info("Placing Order Request: {}", orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating Order with Status CREATED");

        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .OrderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .orderDate(Instant.now())
                .build();
        Order orderSaved = orderRepository.save(order);

        log.info("Calling Payment Service to Complete the Payment");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderSaved.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .referenceNumber(UUID.randomUUID().toString())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing the Order status to PLACED");
            orderStatus = "PLACED";
        }catch (Exception e) {
            log.info("Error occured in payment. Changing the Order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        orderSaved.setOrderStatus(orderStatus);
        orderRepository.save(orderSaved);

        log.info("Place order saved seccessfully {}", orderSaved.getId());
        return orderSaved.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for Order Id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(
                        ()-> new CustomException("Order not found for the orderId :" +orderId, "NOT_FOUND", 404)
                );

        log.info("Invoking Product service to fetch the product for id: {}", order.getProductId());

        OrderResponse.ProductDetails productDetails =
                restTemplate.getForObject(
                        "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                        OrderResponse.ProductDetails.class
                );

        log.info("Getting payment information form the payment Service");

        OrderResponse.PaymentDetails paymentDetails =
                restTemplate.getForObject(
                        "http://PAYMENT-SERVICE/payment/order/"+order.getId(),
                        OrderResponse.PaymentDetails.class
                );

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
    }
}
