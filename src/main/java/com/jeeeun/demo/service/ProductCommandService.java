package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.member.Member;
import com.jeeeun.demo.domain.product.*;
import com.jeeeun.demo.repository.*;
import com.jeeeun.demo.service.product.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductCommandService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionDetailRepository productOptionDetailRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductStockRepository productStockRepository;

    // 상품 등록에 대한 api
    // variants 는 따로 api 구현
    @Transactional
    public ProductCreateResult createProduct(ProductCreateCommand command) {

        // 1. 카테고리, 멤버 조회 (FK 검증)
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // ★ 2. 할인 조건부 검증
        if (command.isDiscounted()) {
            if (command.discountRate() == null) {
                throw new BusinessException(ErrorCode.INVALID_DISCOUNT_RATE);
            }
            if (command.discountStartAt() == null || command.discountEndAt() == null) {
                throw new BusinessException(ErrorCode.INVALID_DISCOUNT_PERIOD);
            }
            // 할인 종료일이 시작일보다 이후인지 확인
            if (command.discountStartAt().isAfter(command.discountEndAt())) {
                throw new BusinessException(ErrorCode.INVALID_DISCOUNT_PERIOD);
            }
        }

        // 3. Product 저장
        Product product = Product.builder()
                .category(category)
                .member(member)
                .name(command.name().trim())
                .description(command.description().trim())
                .salePrice(command.salePrice())
                .isDiscounted(command.isDiscounted())
                .discountRate(command.discountRate())
                .discountStartAt(command.discountStartAt())
                .discountEndAt(command.discountEndAt())
                .isDeleted(false)
                .build();

        productRepository.save(product);

        // 4. 이미지 저장
        for (ProductCreateCommand.ProductImageCommand img : command.images()) {
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(img.imageUrl().trim())
                    .imageOrder(img.imageOrder())
                    .build();

            productImageRepository.save(image);
        }

        // 5. 옵션과 옵션 디테일 저장
        for (ProductCreateCommand.ProductOptionCommand opt : command.options()) {
            ProductOption option = ProductOption.builder()
                    .product(product)
                    .name(opt.optionName().trim())
                    .build();

            productOptionRepository.save(option);

            for (String detail : opt.optionDetails()) {
                ProductOptionDetail optionDetail = ProductOptionDetail.builder()
                        .productOption(option)
                        .description(detail.trim())
                        .build();

                productOptionDetailRepository.save(optionDetail);
            }
        }

        return ProductCreateResult.from(product);
    }


    // variants 등록에 대한 api
    @Transactional
    public ProductVariantCreateResult createVariant(ProductVariantCreateCommand command) {

        // 1. Product 조회 (FK 검증)
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PRODUCT));

        // 2. optionDetailIds 존재 검증
        List<Integer> ids = command.optionDetailIds();

        // TODO ★ 입력값에 대한 검증은 해당 모델에 validate 함수 만들고 router 에서 호출

        if (ids.isEmpty() || ids.size() > 3) {
            throw new BusinessException(ErrorCode.INVALID_OPTION_DETAIL_IDS);
        }

        // 3. optionDetail 조회 (DB 에서)
        List<ProductOptionDetail> details       // ┌ 없는 거 빼고 가져오니 개수로 검증
                = productOptionDetailRepository.findAllById(ids); // command.optionDetailIds()

        // TODO details 빈 리스트인지 아닌지 검사해주는게 좋음

        if (details.isEmpty() || ids.size() != details.size()) {
            throw new BusinessException(ErrorCode.OPTION_DETAIL_REQUIRED);
        }

        // 4. id 오름차순 정렬
        // 요청이 [6, 3, 1] 로 와도 [1, 3, 6]으로 통일해 저장하려고 함!
        details.sort(Comparator.comparing(ProductOptionDetail::getId));
                                        /* └ ProductOptionDetail 에서 getId()
                                            람다 형식 ▶ comparing(detail -> detail.getId())

                                            클래스명::메서드명
                                            ex) Integer::parseInt = Integer.parseInt()

                                            메서드 참조는 람다의 '축약형'

                                            • 람다 (Lambda)
                                            detail -> detail.getId()
                                            • 메서드 참조 (Method Reference)
                                            ProductOptionDetail::getId
                                            ▶ 기능적으로 차이 없고 의미 완전 동일
                                            다만, 메서드 참조는 이미 있는 메서드를 그대로 써야 해서
                                            조금이라도 로직이 추가되는 경우 람다만 사용할 수 있다.
                                         */

        // 5. 엔티티에 optionDetail 1/2/3 매핑
        ProductOptionDetail d1 = details.get(0); // List 인덱스
        ProductOptionDetail d2 = (details.size() >= 2) ? details.get(1) : null;
        ProductOptionDetail d3 = (details.size() == 3) ? details.get(2) : null;

        // 6. 조합명(variantName) 서버에서 생성하기
        String variantName = details.stream()
                .map(ProductOptionDetail::getDescription) // 각 detail 객체에서 description 뽑아 Stream<String>으로 바꿈
                .collect(Collectors.joining(" / ")); // " / " 구분자로 문자열들 합치기
                // ex) details.description = ["blue", "S"] → variantName = "blue / s"

        // ★ Option, OptionDetail 은 상품 등록 때 만들어졌다!

        // 7. ProductVariant 엔티티 생성 후 저장
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .optionDetail1(d1)
                .optionDetail2(d2)
                .optionDetail3(d3)
                .variantName(variantName)
                .additionalPrice(command.additionalPrice())
                .build();

        ProductVariant saved = productVariantRepository.save(variant);

        // 8. 결과 반환
        return ProductVariantCreateResult.from(saved);
    }


    // 상품 재고 설정에 대한 api
    @Transactional
    public StockUpdateResult updateStock(StockUpdateCommand command) {

        // variant 조회
        ProductVariant variant = productVariantRepository.findById(command.variantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_VARIANT));

        // variantId로 stock 조회 후 재고 설정

        // 1. 없으면 새 재고 row 생성 후 저장              // ┌ Jpa 가 찾을 수 있어야 함.
        ProductStock stock = productStockRepository.findByProductVariant_Id(command.variantId())
                .orElseGet(() -> productStockRepository.save(ProductStock.create(variant, 0)));
                // variant 재고 없으면 new 재고 엔티티를 하나 만들기

        // 2. 있으면 operation (SET/INCREASE/DECREASE) 적용
        switch (command.operation()) {
            case SET -> stock.setQuantity(command.quantity());
            case INCREASE -> stock.increase(command.quantity());
            case DECREASE -> stock.decrease(command.quantity());
            default -> throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        return StockUpdateResult.from(stock);
    }




}

