package com.jeeeun.kama.domain.shipping;

import java.math.BigDecimal;

// 배송비 계산 정책 담당 클래스
// 엔티티(Shipping)도 아니고, 서비스도 아님! → 순수하게 계산 로직만!
// 추후 배송비 관련 정책이 바뀌어도 이 클래스만 손보면 됨
public class ShippingPolicy {

    // ★ 무료배송 기준 금액
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(50_000);

    // ★ 무료배송 기준 미달 시 부과되는 고정 배송비
    private static final BigDecimal DEFAULT_SHIPPING_FEE = BigDecimal.valueOf(3_000);

    // NOTE : private 생성자로 인스턴스 생성 막기
    // 계산 기능만 제공하는 유틸 클래스라 new ShippingPolicy()처럼 객체로 만들어 쓸 일이 없음.
    private ShippingPolicy () {}

    // ★ 주문 총액(totalPrice) 받아서 배송비 계산해주는 메서드
    public static BigDecimal calculateFee(BigDecimal totalPrice) {

        // 5만원 이상 무료배송
        if (totalPrice.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }

        // 5만원 미만 배송비 부과
        return DEFAULT_SHIPPING_FEE;
    }

    // compareTo() : 두 값, 혹은 객체 비교 → 같으면 0 / 기준 대상이 크면 양수 / 비교 대상이 크면 음수
    // BigDecimal은 ==, >, < 같은 비교 연산자 사용 불가 (참조 타입이기 때문)
}
