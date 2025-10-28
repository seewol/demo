package com.jeeeun.demo.service;

import com.jeeeun.demo.controller.response.ProductResponse;
import com.jeeeun.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductQueryService {

    private final ProductRepository productRepository;

    // 상품 목록 조회
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> ProductResponse.builder()
                        .productId(product.getProductId())
                        .categoryId(product.getCategory())
                        .productName(product.getProductName())
                        .productContent(product.getProductContent())
                        .originalPrice(product.getOriginalPrice())
                        .salePrice(product.getSalePrice())
                        .isDiscounted(product.isDiscounted())
                        .discountRate(product.getDiscountRate())
                        .discountStartAt(product.getDiscountStartAt())
                        .discountEndAt(product.getDiscountEndAt())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getCreatedAt())
                        .isDeleted(product.isDeleted())
                        .build())
                .toList();
    }

}
