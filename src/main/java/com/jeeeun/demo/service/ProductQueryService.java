package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.controller.response.ProductResponse;
import com.jeeeun.demo.domain.product.Product;
import com.jeeeun.demo.domain.product.ProductImage;
import com.jeeeun.demo.domain.product.ProductOptionDetail;
import com.jeeeun.demo.repository.ProductImageRepository;
import com.jeeeun.demo.repository.ProductOptionDetailRepository;
import com.jeeeun.demo.repository.ProductRepository;
import com.jeeeun.demo.service.product.model.ProductDetailResult;
import com.jeeeun.demo.service.product.model.ProductResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionDetailRepository productOptionDetailRepository;

    // 상품 목록 조회에 대한 api 생성
    @Transactional(readOnly = true)
    public List<ProductResult> getProducts() {
        return productRepository.findAllByCategory()
                .stream()
                .map(ProductResult::from)
                .toList();
    }

    // 상품 상세 조회에 대한 api 생성
    @Transactional(readOnly = true)
    public ProductDetailResult getProductDetail(Integer productId) {

        Product product = productRepository.findDetailWithOptions(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PRODUCT));

        List<ProductImage> images = productImageRepository.findAllByProductId(productId);

        List<ProductOptionDetail> details = productOptionDetailRepository.findAllByProductId(productId);

        // 구조 : optionId -> detail 리스트가 되도록 그룹핑
        // ex. 10 → [Red, Blue] / 20 → [S, M]
        Map<Integer, List<ProductOptionDetail>> detailMap =
                details.stream().collect(Collectors.groupingBy(d -> d.getProductOption().getId()));

        return ProductDetailResult.from(product, images, detailMap);
    }
}
