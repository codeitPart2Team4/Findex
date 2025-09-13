package com.codeit.findex.syncjob.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SyncJobDto(
        Long id,
        String jobType,
        Long indexInfoId,
        LocalDate targetDate,
        String worker,
        LocalDateTime jobTime,
        String result
) {}
