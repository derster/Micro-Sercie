package com.derster.oderService.service;

import com.derster.oderService.model.OrderRequest;
import com.derster.oderService.model.OrderResponse;

public interface OrderService {
    long pladeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
