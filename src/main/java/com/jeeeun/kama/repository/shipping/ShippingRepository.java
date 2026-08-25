package com.jeeeun.kama.repository.shipping;

import com.jeeeun.kama.domain.shipping.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {
    // ★ 현재 findById() 만 있으면 충분 ! → 관리자가 shippingId로 상태 변경할 때 씀
    // 추후 '주문 상세'에서 '배송 상태'별로 '필터링' 요구사항 반영 시
    // 그때 findByStatus() 같은 메서드 추가하기로 하자. (관리자 입장에서는 필요할 지도)
}
