package com.codeit.findex.scheduler;

import com.codeit.findex.data.ApiAutoSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ApiScheduler {
    private final ApiAutoSyncService apiAutoSyncService;

//    @Scheduled(initialDelay = 5000) // 테스트용 어노테이션
    @Scheduled(cron = "0 0 0 * * *")
    public void task() throws Exception {
        System.out.println("오전 9시 30분 지수 데이터 연동:" + LocalDateTime.now());
        apiAutoSyncService.fetchAndProcessAll();
    }
}
