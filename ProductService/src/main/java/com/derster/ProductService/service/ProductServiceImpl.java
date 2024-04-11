package com.derster.ProductService.service;

import com.derster.ProductService.entity.Product;
import com.derster.ProductService.exception.ProductServiceCustomException;
import com.derster.ProductService.model.ProductRequest;
import com.derster.ProductService.model.ProductResponse;
import com.derster.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product");
        Product product = Product.builder()
                .productName(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();
        productRepository.save(product);

        log.info("Product created");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for productId: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(()-> new ProductServiceCustomException("Protuct with given id not exist", "PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);

        return productResponse;
    }

    @Override
    public List<ProductResponse> getAllProduct() {
        log.info("Get all products");

        /*List<ProductResponse> products = productRepository.findAll().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());*/

        List<ProductResponse> products = productRepository.findAll().stream()
                .map(product -> mapProductToProductResponse(product))
                .collect(Collectors.toList());
        return products;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id: {}", quantity, productId);

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductServiceCustomException("Product with given Id not found", "PRODUCT_NOT_FOUND"));

        if (product.getQuantity()<quantity){
            throw new ProductServiceCustomException(
                    "Product does not have sufficient Quantity",
                    "INSUFFICIENT_QUANTITY"
            );
        }
        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);
        log.info("Product Quantity updated Successfully");
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        return ProductResponse.builder()
                .productName(product.getProductName())
                .productId(product.getProductId())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}
