package com.codeit.findex.data.scheduler;

import com.codeit.findex.data.AutoIndexDataSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class IndexApiScheduler {
    private final AutoIndexDataSyncService autoIndexSyncService;

    @Scheduled(cron = "0 0 0 * * *")
    public void task() throws Exception {
        System.out.println("오전 9시 30분 지수 데이터 연동:" + LocalDateTime.now());
        autoIndexSyncService.fetchAndProcessAll();
    }
}
