package com.derster.ProductService.service;

import com.derster.ProductService.model.ProductRequest;
import com.derster.ProductService.model.ProductResponse;

import java.util.List;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    List<ProductResponse> getAllProduct();

    void reduceQuantity(long productId, long quantity);
}
