package com.codeit.findex.syncjob.repository;

import com.codeit.findex.common.enums.SortDirection;
import com.codeit.findex.syncjob.entity.QSyncJob;
import com.codeit.findex.syncjob.entity.SyncJob;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SyncJobQueryRepositoryImpl implements SyncJobQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SyncJob> findAllByConditions(
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
    ) {
        QSyncJob syncJob = QSyncJob.syncJob;

        BooleanBuilder filters = new BooleanBuilder();
        if (jobType != null && !jobType.isBlank()) {
            filters.and(syncJob.jobType.containsIgnoreCase(jobType));
        }
        if (indexInfoId != null) {
            filters.and(syncJob.indexInfo.id.eq(indexInfoId));
        }
        if(baseDateFrom != null) {
            filters.and(syncJob.targetDate.goe(baseDateFrom));
        }
        if(baseDateTo != null) {
            filters.and(syncJob.targetDate.loe(baseDateTo));
        }
        if(worker != null && !worker.isBlank()) {
            filters.and(syncJob.worker.containsIgnoreCase(worker));
        }
        if (jobTimeFrom != null) {
            filters.and(syncJob.jobTime.goe(jobTimeFrom));
        }
        if (jobTimeTo != null) {
            filters.and(syncJob.jobTime.loe(jobTimeTo));
        }
        if(status != null && !status.isBlank()) {
            filters.and(syncJob.result.containsIgnoreCase(status));
        }
        if(idAfter != null) {
            filters.and(syncJob.id.gt(idAfter));
        }

        BooleanBuilder conditions = new BooleanBuilder(filters);
        applyCursor(conditions, cursor, sortField, sortDirection, syncJob);


        return queryFactory
                .selectFrom(syncJob)
                .where(conditions)
                .orderBy(buildOrderSepcifiers(sortField, sortDirection, syncJob).toArray(new OrderSpecifier[0]))
                .limit(size + 1)
                .fetch();
    }

    private void applyCursor(BooleanBuilder filters, String cursor, String sortBy, SortDirection sortDirection, QSyncJob syncJob) {
        if (cursor == null) return;

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "jobTime";
        }

        if (sortDirection == null) {
            sortDirection = SortDirection.desc;
        }

        String[] parts = cursor.split(":");
        String cursorValue = parts[0];
        Long cursorId = Long.parseLong(parts[1]);

        if ("jobTime".equals(sortBy)) {
            filters.and(
                    sortDirection == SortDirection.desc
                        ? syncJob.jobTime.lt(LocalDateTime.parse(cursorValue)).or(syncJob.jobTime.eq(LocalDateTime.parse(cursorValue)).and(syncJob.id.lt(cursorId)))
                        : syncJob.jobTime.gt(LocalDateTime.parse(cursorValue)).or(syncJob.jobTime.eq(LocalDateTime.parse(cursorValue)).and(syncJob.id.gt(cursorId)))
            );
        } else if ("targetDate".equals(sortBy)) {
            filters.and(
                    sortDirection == SortDirection.desc
                            ? syncJob.targetDate.lt(LocalDate.parse(cursorValue)).or(syncJob.targetDate.eq(LocalDate.parse(cursorValue)).and(syncJob.id.lt(cursorId)))
                            : syncJob.targetDate.gt(LocalDate.parse(cursorValue)).or(syncJob.targetDate.eq(LocalDate.parse(cursorValue)).and(syncJob.id.gt(cursorId)))
            );
        } else {
            filters.and(sortDirection == SortDirection.desc ? syncJob.id.lt(cursorId) : syncJob.id.gt(cursorId));
        }
    }

    private List<OrderSpecifier<?>> buildOrderSepcifiers(String sortBy, SortDirection sortDirection, QSyncJob syncJob) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if ("jobTime".equals(sortBy)) {
            orderSpecifiers.add(sortDirection == SortDirection.desc ? syncJob.jobTime.desc() : syncJob.jobTime.asc());
        } else if ("targetDate".equals(sortBy)) {
            orderSpecifiers.add(sortDirection == SortDirection.desc ? syncJob.targetDate.desc() : syncJob.targetDate.asc());
        }

        orderSpecifiers.add(sortDirection == SortDirection.desc ? syncJob.id.desc() : syncJob.id.asc());

        return orderSpecifiers;
    }

    @Override
    public long countByConditions(String jobType,
                                  Long indexInfoId,
                                  LocalDate baseDateFrom,
                                  LocalDate baseDateTo,
                                  String worker,
                                  String status,
                                  LocalDateTime jobTimeFrom,
                                  LocalDateTime jobTimeTo) {
        QSyncJob syncJob = QSyncJob.syncJob;

        BooleanBuilder filters = new BooleanBuilder();
        if (jobType != null && !jobType.isBlank()) {
            filters.and(syncJob.jobType.containsIgnoreCase(jobType));
        }
        if (indexInfoId != null) {
            filters.and(syncJob.indexInfo.id.eq(indexInfoId));
        }
        if(baseDateFrom != null) {
            filters.and(syncJob.targetDate.goe(baseDateFrom));
        }
        if(baseDateTo != null) {
            filters.and(syncJob.targetDate.loe(baseDateTo));
        }
        if(worker != null && !worker.isBlank()) {
            filters.and(syncJob.worker.containsIgnoreCase(worker));
        }
        if(status != null && !status.isBlank()) {
            filters.and(syncJob.result.containsIgnoreCase(status));
        }
        if (jobTimeFrom != null) {
            filters.and(syncJob.jobTime.goe(jobTimeFrom));
        }
        if (jobTimeTo != null) {
            filters.and(syncJob.jobTime.loe(jobTimeTo));
        }

        Long cnt = queryFactory
                .select(syncJob.count())
                .from(syncJob)
                .where(filters)
                .fetchOne();

        return cnt != null ? cnt : 0L;
    }
}
