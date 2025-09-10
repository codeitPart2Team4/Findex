package com.codeit.findex.autosync.repository;

import com.codeit.findex.autosync.entity.AutoSyncConfig;
import com.codeit.findex.autosync.entity.QAutoSyncConfig;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AutoSyncConfigQueryRepositoryImpl implements AutoSyncConfigQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AutoSyncConfig> findByCursor(Long indexInfoId, Boolean enabled, Long idAfter, int size, Sort.Direction sortDirection) {
        QAutoSyncConfig config = QAutoSyncConfig.autoSyncConfig;
        BooleanBuilder where = new BooleanBuilder();

        if (indexInfoId != null) {
            where.and(config.indexInfo.id.eq(indexInfoId));
        }
        if (enabled != null) {
            where.and(config.enabled.eq(enabled));
        }
        if (idAfter != null) {
            if (sortDirection.isAscending()) {
                where.and(config.id.gt(idAfter));
            } else {
                where.and(config.id.lt(idAfter));
            }
        }

        return queryFactory.selectFrom(config)
                .where(where)
                .orderBy(sortDirection.isAscending() ? config.id.asc() : config.id.desc())
                .limit(size)
                .fetch();
    }
}
