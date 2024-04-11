package com.derster.ProductService.model;

import com.derster.ProductService.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String productName;
    private long productId;
    private long price;
    private long quantity;

    public ProductResponse(Product product) {
        this.productId = product.getProductId();
        this.price = product.getPrice();
        this.productName = product.getProductName();
        this.quantity = product.getQuantity();
    }
}
