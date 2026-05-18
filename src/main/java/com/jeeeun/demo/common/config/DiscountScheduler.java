package com.jeeeun.demo.common.config;

import com.jeeeun.demo.repository.product.ProductRepository;
import com.jeeeun.demo.repository.user.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscountScheduler {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    // 상품 할인 기간 관리
    // 매일 자정에 자동 실행되도록 할 계획 (00:00:00)
    // cron : "초 분 시 일 월 요일"
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateDiscountStatus() {

        LocalDateTime now = LocalDateTime.now();

        // 할인 기간 지남 → isDiscounted = false
        int expiredCount = productRepository.bulkExpireDiscounts(now);

        // 할인 기간 시작 → isDiscounted = true
        int startedCount = productRepository.bulkStartDiscounts(now);

        log.info("[할인 스케줄러] 만료: {}건, 시작: {}건", expiredCount, startedCount);
    }


    // 장바구니 자동 만료
    // 매일 새벽 2시에 자동 실행 (02:00:00)
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void expiredOldCartItem() {

        // 30일 전 시간을 계산해야 함
        // ex) 지금 : 5월 18일 00:00 → threshold : 4월 18일 00:00
        // 고로 updatedAt이 그 이전인 것들은 30일 넘게 방치된 것!
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);

        int deletedCount = cartItemRepository.deleteExpiredCartItems(threshold);

        log.info("[장바구니 만료 스케줄러] 삭제: {}건", deletedCount);
    }


}
