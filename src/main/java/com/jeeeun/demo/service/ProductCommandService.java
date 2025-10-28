package com.jeeeun.demo.service;

import com.jeeeun.demo.controller.request.ProductCreateRequest;
import com.jeeeun.demo.controller.request.ProductRequest;
import com.jeeeun.demo.controller.response.ProductCreateResponse;
import com.jeeeun.demo.domain.Product;
import com.jeeeun.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductCommandService {

    private final ProductRepository productRepository;
    // 상품 등록
//    public ProductCreateResponse createProduct(ProductCreateRequest request) {
//
//        Product saved = productRepository.save(
//                Product.builder()
//
//        )
//    }
}
