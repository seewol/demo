package com.jeeeun.demo.service;

import com.jeeeun.demo.controller.request.ProductCreateRequest;
import com.jeeeun.demo.controller.response.ProductCreateResponse;
import com.jeeeun.demo.domain.*;
import com.jeeeun.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductCommandService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionDetailRepository productOptionDetailRepository;

    // 상품 등록에 대한 api 생성
    public ProductCreateResponse createProduct(ProductCreateRequest req) {

        // 할인 여부 검증

        // 1. Product 먼저 구성 후 저장
        Product product = Product.builder()
                .category(categoryRepository.findById(req.getCategoryId()).orElseThrow())
                .member(memberRepository.findById(req.getMemberId()).orElseThrow())
                .productName(req.getProductName())
                .productContent(req.getProductContent())
                .originalPrice(req.getOriginalPrice())
                .salePrice(req.getSalePrice())
                .isDiscounted(req.isDiscounted())
                .discountRate(req.getDiscountRate())
                .discountStartAt(req.getDiscountStartAt())
                .discountEndAt(req.getDiscountEndAt())
                .build();

        Product saved = productRepository.save(product);
        // saved 객체에는 DB 에서 생성된 product_id 값이 들어있음.

        // 그래서 image.setProduct(saved) 하게 되면,
        // JPA 가 자동으로 그 id를 FK로 사용해 준다.
        // → DB insert 시 image.product_id = saved.product_id

        // 아래 .product(saved) 는 → image.product = saved 세팅
        // JPA 가 자동으로 product_id = saved.getProductId()로 매핑하고 insert 시 FK 로 넣음.

        // 2. 이미지 리스트 별도 저장
        for (ProductCreateRequest.ProductImageRequest imageRequest : req.getImages()) {
            ProductImage image = ProductImage.builder()
                    .product(saved) // 자동으로 product_id를 FK 로 매핑
                    .imageUrl(imageRequest.getImageUrl())
                    .imageOrder(imageRequest.getImageOrder())
                    .build();
            productImageRepository.save(image);
        }

//        // 3. 옵션 리스트 별도 저장
//        for (ProductCreateRequest.ProductOptionRequest optionRequest : req.getOptions()) {
//
//            // 3-1. 옵션 그룹 저장
//            ProductOption option = ProductOption.builder()
//                    .product(saved) // 자동으로 product_id를 FK 로 매핑
//                    .optionName(optionRequest.getOptionName())
//                    .build();
//            productOptionRepository.save(option);
//
//            // 3-2. 옵션 그룹의 세부값 저장 및 생성
//            for (String detailContent : optionRequest.getOptionDetails()) {
//                ProductOptionDetail optionDetail = ProductOptionDetail.builder()
//                        .productOption(option) // 자동으로 option_id를 FK 로 매핑
//                        .optionContent(detailContent)
//                        .build();
//                productOptionDetailRepository.save(optionDetail);
//        }
//
//        // 4. 조합 리스트 별도 저장
//        for (ProductCreateRequest.ProductVariantRequest variantRequest : req.getVariants()) {
//
//            // 각 옵션의 디테일 이름으로 실제 엔티티 찾기
//            ProductOptionDetail detail1 = null;
//            ProductOptionDetail detail2 = null;
//            ProductOptionDetail detail3 = null;
//
//            if (variantRequest.getOptionDetail1() != null) {
//                detail1 = productOptionDetailRepository
//                        .findByProductAndOptionContent(saved, variantRequest.getOptionDetail1())
//                        .
//
//            }
//
//        }

        return ProductCreateResponse.example();
    }


}
