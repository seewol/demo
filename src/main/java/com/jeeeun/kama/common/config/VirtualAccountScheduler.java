package com.jeeeun.kama.common.config;

import com.jeeeun.kama.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VirtualAccountScheduler {

    private final OrderCommandService orderCommandService;

    // 가상계좌 입금기한 만료 처리
    // 매시 정각마다 자동 실행함 (매시간 0분 0초)
    @Scheduled(cron = "0 0 * * * *")    // cron : "초 분 시 일 월 요일"
    public void expiredOverdueVirtualAccountOrders() {
        int expiredCount = orderCommandService.expireVirtualAccountOrders();

        log.info("[가상계좌 만료 스케줄러] 만료 처리 : {}건", expiredCount);
    }
}
