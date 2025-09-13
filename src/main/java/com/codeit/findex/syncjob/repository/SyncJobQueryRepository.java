package com.codeit.findex.syncjob.repository;

import com.codeit.findex.common.enums.SortDirection;
import com.codeit.findex.syncjob.entity.SyncJob;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SyncJobQueryRepository {

    List<SyncJob> findAllByConditions(
            String jobType,
            Long indexInfoId,
            LocalDate baseDateFrom,
            LocalDate baseDateTo,
            String worker,
            LocalDateTime jobTimeFrom,
            LocalDateTime jobTimeTo,
            String status,
            Long idAfter,
            String cursor,
            String sortField,
            SortDirection sortDirection,
            int size
    );

    long countByConditions(
            String          jobType,
            Long            indexInfoId,
            LocalDate       baseDateFrom,
            LocalDate       baseDateTo,
            String          worker,
            String          status,
            LocalDateTime   jobTimeFrom,
            LocalDateTime   jobTimeTo
    );
}
