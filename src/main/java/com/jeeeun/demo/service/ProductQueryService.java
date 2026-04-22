package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.product.Product;
import com.jeeeun.demo.domain.product.ProductImage;
import com.jeeeun.demo.domain.product.ProductOptionDetail;
import com.jeeeun.demo.repository.product.ProductImageRepository;
import com.jeeeun.demo.repository.product.ProductOptionDetailRepository;
import com.jeeeun.demo.repository.product.ProductRepository;
import com.jeeeun.demo.service.product.model.ProductDetailResult;
import com.jeeeun.demo.service.product.model.ProductResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<ProductResult> getProducts(String keyword, Long categoryId, Boolean isDiscounted, Pageable pageable) {

        // productRepository가 ProductRepositoryCustom 상속중
        return productRepository.searchProducts(keyword, categoryId, isDiscounted, pageable)
                .map(ProductResult::from);  // Page 안의 각 Product → ProductResult 변환

    }


    // 상품 상세 조회에 대한 api 생성
    @Transactional(readOnly = true)
    public ProductDetailResult getProductDetail(Long productId) {

        Product product = productRepository.findDetailWithOptions(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PRODUCT));

        List<ProductImage> images = productImageRepository.findAllByProductId(productId);

        List<ProductOptionDetail> details = productOptionDetailRepository.findAllByProductId(productId);

        // 구조 : optionId -> detail 리스트가 되도록 그룹핑
        // ex ≫ 10 → [Red, Blue] / 20 → [S, M]
        Map<Long, List<ProductOptionDetail>> detailMap =
                details.stream().collect(Collectors.groupingBy(d -> d.getProductOption().getId()));

        return ProductDetailResult.from(product, images, detailMap);
    }
}
