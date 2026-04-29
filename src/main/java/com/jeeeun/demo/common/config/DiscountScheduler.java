package com.jeeeun.demo.common.config;

import com.jeeeun.demo.repository.product.ProductRepository;
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
}
