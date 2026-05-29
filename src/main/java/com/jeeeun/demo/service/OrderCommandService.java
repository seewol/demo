package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderItem;
import com.jeeeun.demo.domain.product.Product;
import com.jeeeun.demo.domain.product.ProductImage;
import com.jeeeun.demo.domain.product.ProductStock;
import com.jeeeun.demo.domain.product.ProductVariant;
import com.jeeeun.demo.domain.user.CartItem;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.external.portone.PortOneClient;
import com.jeeeun.demo.repository.order.OrderRepository;
import com.jeeeun.demo.repository.product.ProductImageRepository;
import com.jeeeun.demo.repository.product.ProductStockRepository;
import com.jeeeun.demo.repository.product.ProductVariantRepository;
import com.jeeeun.demo.repository.user.CartItemRepository;
import com.jeeeun.demo.repository.user.UserRepository;
import com.jeeeun.demo.service.order.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductStockRepository productStockRepository;
    private final ProductImageRepository productImageRepository;

    private final PortOneClient portOneClient;
    private final ProductVariantRepository productVariantRepository;

    // 주문 생성 (장바구니)
    @Transactional
    public OrderCreateResult createOrder(OrderCreateCommand command) {

        // ★ 1 : 포트원 결제 검증
        PortOnePaymentResponse.PortOnePaymentBody payment =
                portOneClient.getPayment(command.impUid());

        // 결제 상태 ≠ "paid" → 결제 미완료 판단 → 예외
        if (!"paid".equals(payment.status())) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT);
        }

        // 포트원에서 조회한 실 결제 금액
        BigDecimal paidAmount = payment.amount();

        try {
            // ★ 2 : 유저 조회
            User user = userRepository.findByIdAndIsDeletedFalse(command.userId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

            // ★ 3 : 선택한 cartItem 목록 조회 (주문할 것들)
            List<CartItem> cartItems = cartItemRepository.findAllById(command.cartItemIds());

            // ★ 3-1 : 요청한 cartItemIds 수 ≠ 실제 조회된 수 → 예외
            // ex) 존재하지 않는 cartItemId가 포함된 경우
            if (cartItems.size() != command.cartItemIds().size()) {
                throw new BusinessException(ErrorCode.NOT_FOUND_CART_ITEM);
            }

            // ★ 4 : 재고 확인 + 총 금액 계산
            BigDecimal totalPrice = BigDecimal.ZERO;        // 0원으로 시작
            List<ProductStock> stocks = new ArrayList<>();  // 재고 객체들 보관

            for (CartItem cartItem : cartItems) {

                // 삭제된 상품인지 확인 (장바구니에 담은 후 삭제됐을 수 있음!)
                Product cartProduct = cartItem.getProductVariant().getProduct();

                if (cartProduct.isDeleted()) {
                    throw new BusinessException(ErrorCode.ALREADY_DELETED_PRODUCT);
                }

                // 해당 cartItem의 variant 재고 조회 (차감은 재고 검증 이후 6번에서!)
                ProductStock stock = productStockRepository
                        .findByProductVariant_Id(cartItem.getProductVariant().getId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.OUT_OF_STOCK));

                // 재고 수량 < 주문 수량이면 예외
                if (stock.getQuantity() < cartItem.getQuantity()) {
                    throw new BusinessException(ErrorCode.OUT_OF_STOCK);
                }

                stocks.add(stock);  // 이후 재고 차감시 쓸 재고 객체 저장

                BigDecimal discountedPrice = calculateDiscountedPrice(cartItem);

                // 총 금액에 누적 (할인가 * 수량)
                totalPrice = totalPrice.add(
                        discountedPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                // note : 9,000원 * 2개 = 18,000원 → totalPrice에 더하기
            }

            // ★ 4-1 : 실 결제된 금액 == 계산한 totalPrice 검증
            // 금액 조작을 방지하기 위해 반드시 검증이 필요하다.
            if (paidAmount.compareTo(totalPrice) != 0) {
                throw new BusinessException(ErrorCode.INVALID_PAYMENT);
            }
            // BigDecimal 값은 compareTo()로 비교해야 함.
            // 소수점 자릿수 때문인데 이는 순수하게 값만 비교, 같으면 0 반환

            // ★ 5 : Order 생성
            Order order = orderRepository.save(Order.from(user, totalPrice, payment.impUid()));

            // ★ 6 : OrderItem 생성 및 재고 차감
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem cartItem = cartItems.get(i);

                ProductVariant variant = cartItem.getProductVariant();
                Product product = variant.getProduct();

                // 정가 : 주문 시점 가격 고정 (스냅샷 의미)
                BigDecimal unitPrice = product.getSalePrice()
                        .add(variant.getAdditionalPrice() != null ? variant.getAdditionalPrice() : BigDecimal.ZERO);

                // 할인가 계산
                BigDecimal discountedPrice = calculateDiscountedPrice(cartItem);

                // 썸네일 조회
                String thumbnailUrl = productImageRepository.findThumbnailByProductId(product.getId())
                        .map(ProductImage::getImageUrl)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_IMAGE));

                // ★ OrderItem 생성 → Order에 추가
                // cascade = ALL 이라 Order 저장시 OrderItem 자동 저장
                order.getOrderItems().add(
                        OrderItem.from(
                                order,
                                variant,
                                cartItem.getQuantity(),
                                product.getName(),
                                variant.getVariantName(),
                                unitPrice,
                                discountedPrice,
                                thumbnailUrl
                        )
                );
                // note : 주문 생성 시점에 상품명, 조합명, 썸네일 스냅샷으로 저장
                // 조회 시 DB 추가 조회 없이 바로 꺼내 사용할 수 있다.

                // ★ 7 : 재고 차감
                stocks.get(i).decrease(cartItem.getQuantity());
                // 앞서 저장해둔 재고 객체
            }

            // ★ 8 : 주문한 CartItem은 삭제
            cartItemRepository.deleteAll(cartItems);
            // 리스트 넘겨서 한 번에 삭제

            return OrderCreateResult.from(order);

        } catch (BusinessException e) {
            // ★ 결제는 성공했으나 주문 생성 실패 → 포트원에 자동 환불 요청
            // 환불 후 예외 다시 던져서 @Transactional 롤백을 유도해야 함.
            portOneClient.cancelPayment(command.impUid());
            throw e;
        }


    }


    // 주문 생성 (바로구매)
    public OrderCreateResult createDirectOrder(DirectOrderCreateCommand command) {

        // ★ 1 : 포트원 결제 검증
        PortOnePaymentResponse.PortOnePaymentBody payment =
                portOneClient.getPayment(command.impUid());

        if (!"paid".equals(payment.status())) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT);
        }

        BigDecimal paidAmount = payment.amount();   // 결제 금액

        // NOTE: 결제 검증 통과 후 로직 ─ 실패 시 자동 환불
        try {

            // ★ 2 : 유저 조회
            User user = userRepository.findByIdAndIsDeletedFalse(command.userId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

            // ★ 3 : ProductVariant 조회
            // 장바구니 주문은 CartItem → Variant 타고 갔는데,
            // 바로구매는 variantId로 직접 조회해야 한다.
            ProductVariant variant = productVariantRepository.findById(command.variantId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_VARIANT));

            Product product = variant.getProduct();

            // ★ 3-1 : 삭제된 상품인지 확인
            if (product.isDeleted()) {
                throw new BusinessException(ErrorCode.ALREADY_DELETED_PRODUCT);
            }

            // ★ 4 : maxPurchaseQuantity 검증
            // 장바구니는 담을 때 검증하지만, 이건 안 거치니까 여기서 직접 검증
            // null이면 제한 없음 → 검증 스킵
            Integer maxPurchaseQuantity = product.getMaxPurchaseQuantity();
            if (maxPurchaseQuantity != null && command.quantity() > maxPurchaseQuantity) {
                throw new BusinessException(ErrorCode.EXCEEDED_PURCHASE_LIMIT);
            }

            // ★ 5 : 재고 확인
            ProductStock stock = productStockRepository
                    .findByProductVariant_Id(command.variantId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.OUT_OF_STOCK));

            if (stock.getQuantity() < command.quantity()) {
                throw new BusinessException(ErrorCode.OUT_OF_STOCK);
            }

            // ★ 6 : 금액 계산
            // 정가 (salePrice + additionalPrice)
            BigDecimal additionalPrice = variant.getAdditionalPrice();
            BigDecimal unitPrice = product.getSalePrice()
                    .add(additionalPrice != null ? additionalPrice : BigDecimal.ZERO);

            // 할인가 계산
            BigDecimal discountedPrice;
            if (product.isDiscounted() && product.getDiscountRate() != null) {
                // salePrice만 할인 적용 (additionalPrice는 제외)
                BigDecimal multiplier = BigDecimal.ONE
                        .subtract(BigDecimal.valueOf(product.getDiscountRate())
                                .divide(BigDecimal.valueOf(100)));

                discountedPrice = product.getSalePrice()
                        .multiply(multiplier)
                        .setScale(0, RoundingMode.HALF_UP)
                        .add(additionalPrice != null ? additionalPrice : BigDecimal.ZERO);
            } else {
                discountedPrice = unitPrice;
            }

            // 총 금액 = 할인가 * 수량
            BigDecimal totalPrice = discountedPrice
                    .multiply(BigDecimal.valueOf(command.quantity()));

            // ★ 6-1 : 실 결제 금액 == 계산 금액 검증
            if (paidAmount.compareTo(totalPrice) != 0) {
                throw new BusinessException(ErrorCode.INVALID_PAYMENT);
            }

            // ★ 7 : 썸네일 조회
            String thumbnailUrl = productImageRepository.findThumbnailByProductId(product.getId())
                    .map(ProductImage::getImageUrl)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_IMAGE));

            // ★ 8 : Order 생성
            Order order = orderRepository.save(Order.from(user, totalPrice, payment.impUid()));

            // ★ 9 : OrderItem 생성
            order.getOrderItems().add(
                    OrderItem.from(
                            order,
                            variant,
                            command.quantity(),
                            product.getName(),
                            variant.getVariantName(),
                            unitPrice,
                            discountedPrice,
                            thumbnailUrl
                    )
            );

            // ★ 10 : 재고 차감
            stock.decrease(command.quantity());

            return OrderCreateResult.from(order);

        } catch (BusinessException e) {
            // NOTE : 결제 성공 후 주문 생성 실패 → 포트원 자동 환불
            portOneClient.cancelPayment(command.impUid());
            throw e;
        }

    }


    // 할인 여부 확인 & 적용 후 실결제금액 계산
    private BigDecimal calculateDiscountedPrice(CartItem cartItem) {

        BigDecimal salePrice = cartItem.getProductVariant().getProduct().getSalePrice();
        BigDecimal additionalPrice = cartItem.getProductVariant().getAdditionalPrice();
        boolean isDiscounted = cartItem.getProductVariant().getProduct().isDiscounted();
        Integer discountRate = cartItem.getProductVariant().getProduct().getDiscountRate();

        // 할인가 (salePrice만 할인 적용 / 추가금은 예외)
        if (isDiscounted && discountRate != null) {
            BigDecimal multiplier = BigDecimal.ONE
                    .subtract(BigDecimal.valueOf(discountRate)
                            .divide(BigDecimal.valueOf(100)));

            // note : 정가 10,000원, 할인율 10% → 10000 * (1 - 10/100) = 9,000원

            return salePrice
                    .multiply(multiplier)
                    .setScale(0, RoundingMode.HALF_UP)
                    .add(additionalPrice != null ? additionalPrice : BigDecimal.ZERO);
        }

        // 정가 (할인 X) + 추가금
        return salePrice.add(additionalPrice != null ? additionalPrice : BigDecimal.ZERO);

    }


    // 주문 취소
    @Transactional
    public OrderCancelResult cancelOrder(Long orderId, Long userId) {

        // ★ 1 : 주문 조회
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ORDER));

        // ★ 2 : 본인 주문인지 검증
        if(!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // ★ 3 : 취소 가능 상태인지 검증 + 주문 상태 변경 (CANCELLED)
        order.cancel();

        // ★ 4 : PAID 상태의 주문 → 포트원에 환불 요청
        // PENDING은 결제 전이기 때문에 환불이 불필요.
        if (order.getImpUid() != null) {
            portOneClient.cancelPayment(order.getImpUid());
        }

        // ★ 5 : 취소한 재고만큼 복구
        for (OrderItem item : order.getOrderItems()) {

            productStockRepository
                    .findByProductVariant_Id(item.getProductVariant().getId())
                    .ifPresent(stock -> stock.increase(item.getQuantity()));
                    // 재고 row 있으면 복구, 없으면 스킵
        }

        // note : 재고 없으면 에러 띄우는 방식
        // .orElseThrow(()-> new BusinessException(ErrorCode.OUT_OF_STOCK));
        // stock.increase(item.getQuantity());

        return OrderCancelResult.from(order);
    }

}
