package com.jeeeun.kama.service;

import com.jeeeun.kama.common.error.BusinessException;
import com.jeeeun.kama.common.error.ErrorCode;
import com.jeeeun.kama.domain.shipping.Shipping;
import com.jeeeun.kama.repository.shipping.ShippingRepository;
import com.jeeeun.kama.service.shipping.model.ShippingDelayCommand;
import com.jeeeun.kama.service.shipping.model.ShippingStatusResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShippingCommandService {

    private final ShippingRepository shippingRepository;


    // ★ 배송 시작
    @Transactional
    public ShippingStatusResult ship(Long shippingId) {

        // 1: 배송 조회 (없을 시 예외)
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_SHIPPING));

        // 2: 상태 전이 (PREPARING/DELAYD → SHIPPING)
        // 규칙 위반일 경우 도메인 메서드가 알아서 예외 던짐
        shipping.ship();

        // ★ saveAndFlush로 즉시 DB 반영 → updatedAt이 이 시점 값으로 갱신됨
        Shipping saved = shippingRepository.saveAndFlush(shipping);

        // 3 : save() 명시적 호출 안 해도 됨
        // @Transactional 안에서 조회한 엔티티는 '영속 상태'라,
        // 필드 값만 바꾸면 커밋 시점에 JPA가 자동으로 UPDATE 쿼리 날림 (더티 체킹)
        return ShippingStatusResult.from(saved);

    }


    // NOTE : 영속 상태 (Persistent State)
    // 영속 상태(Persistent State)는 데이터베이스 등의 영구 저장소에서 관리되며,
    // 프로그램이 끝나도 데이터가 사라지지 않고 유지되는 상태를 말함.
    // 대표적으로 JPA(Java Persistence API) 환경에서
    // 영속성 컨텍스트(Persistence Context)가 엔티티 객체를 관리하는 상태

    // NOTE : Dirty Checking ; 더티 체킹
    // JPA에서 더티 체킹(dirty checking)이란 영속성 컨테이너가 관리하는 엔티티의 상태를 감지해서,
    // 변경된 부분이 있다면 자동으로 트랜잭션이 끝나는 시점에 데이터베이스에 반영하는 기능.
    // 따라서 여기서 말하는 dirty는 “엔티티 데이터의 변경된 부분”을 뜻하며,
    // dirty checking은 변경된 부분을 감지한다는 의미


    // ★ 발송 지연 ─ 얘만 바디 있음
    @Transactional
    public ShippingStatusResult delay(ShippingDelayCommand command) {

        Shipping shipping = shippingRepository.findById(command.shippingId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_SHIPPING));

        shipping.delay(command.reason(), command.newExpectedShipDate());

        Shipping saved = shippingRepository.saveAndFlush(shipping);

        return ShippingStatusResult.from(saved);
    }


    // ★ 배송 완료
    @Transactional
    public ShippingStatusResult deliver(Long shippingId) {

        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_SHIPPING));

        shipping.deliver();

        Shipping saved = shippingRepository.saveAndFlush(shipping);

        return ShippingStatusResult.from(saved);
    }
}
