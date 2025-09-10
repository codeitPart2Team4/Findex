package com.codeit.findex.indexinfo.repository;

import com.codeit.findex.indexinfo.enums.SortDirection;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.indexinfo.entity.QIndexInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IndexInfoQueryRepositoryImpl implements IndexInfoQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 인덱스 정보 목록 조회
     */
    @Override
    public List<IndexInfo> findAllByConditions(
            String indexClassification,     // 지수 분류
            String indexName,               // 지수 이름
            Boolean favorite,               // 즐겨찾기 여부
            String cursor,                  // 커서 기반 페이징 (마지막 데이터 위치 표시)
            String sortField,               // 어떤 컬럼으로 정렬할지
            SortDirection sortDirection,    // 정렬 방향 (ASC/DESC)
            int size                        // 몇 개까지 가져올지 (페이지 크기)
    ) {
        QIndexInfo indexInfo = QIndexInfo.indexInfo;

        BooleanBuilder filters = new BooleanBuilder();
        if (indexClassification != null && !indexClassification.isBlank()) {
            filters.and(indexInfo.indexClassification.containsIgnoreCase(indexClassification));
        }
        if (indexName != null && !indexName.isBlank()) {
            filters.and(indexInfo.indexName.containsIgnoreCase(indexName));
        }
        if (favorite != null) {
            filters.and(indexInfo.favorite.eq(favorite));
        }

        BooleanBuilder conditions = new BooleanBuilder(filters);
        applyCursor(conditions, cursor, sortField, sortDirection, indexInfo);

        return queryFactory
                .selectFrom(indexInfo)
                .where(conditions)
                .orderBy(buildOrderSpecifiers(sortField, sortDirection, indexInfo).toArray(new OrderSpecifier[0]))
                .limit(size + 1)
                .fetch();
    }

    /**
     * 커서 조건 추가
     */
    private void applyCursor(BooleanBuilder conditions, String cursor, String sortBy, SortDirection sortDirection, QIndexInfo indexInfo) {
        if (cursor == null) return;

        String[] parts = cursor.split(":");
        String cursorValue = parts[0];
        Long cursorId = Long.parseLong(parts[1]);

        if ("indexClassification".equals(sortBy)) {
            conditions.and(
                    sortDirection == SortDirection.asc
                            ? indexInfo.indexClassification.gt(cursorValue).or(indexInfo.indexClassification.eq(cursorValue).and(indexInfo.id.gt(cursorId)))
                            : indexInfo.indexClassification.lt(cursorValue).or(indexInfo.indexClassification.eq(cursorValue).and(indexInfo.id.lt(cursorId)))
            );
        } else if ("indexName".equals(sortBy)) {
            conditions.and(
                    sortDirection == SortDirection.asc
                            ? indexInfo.indexName.gt(cursorValue).or(indexInfo.indexName.eq(cursorValue).and(indexInfo.id.gt(cursorId)))
                            : indexInfo.indexName.lt(cursorValue).or(indexInfo.indexName.eq(cursorValue).and(indexInfo.id.lt(cursorId)))
            );
        } else if ("employedItemsCount".equals(sortBy)) {
            conditions.and(
                    sortDirection == SortDirection.asc
                            ? indexInfo.employedItemsCount.gt(Integer.parseInt(cursorValue)).or(indexInfo.employedItemsCount.eq(Integer.parseInt(cursorValue)).and(indexInfo.id.gt(cursorId)))
                            : indexInfo.employedItemsCount.lt(Integer.parseInt(cursorValue)).or(indexInfo.employedItemsCount.eq(Integer.parseInt(cursorValue)).and(indexInfo.id.lt(cursorId)))
            );
        } else {
            conditions.and(sortDirection == SortDirection.asc ? indexInfo.id.gt(cursorId) : indexInfo.id.lt(cursorId));
        }
    }

    /**
     * 정렬 조건 생성
     */
    private List<OrderSpecifier<?>> buildOrderSpecifiers(String sortBy, SortDirection sortDirection, QIndexInfo indexInfo) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if ("indexClassification".equals(sortBy)) {
            orders.add(sortDirection == SortDirection.asc ? indexInfo.indexClassification.asc() : indexInfo.indexClassification.desc());
        } else if ("indexName".equals(sortBy)) {
            orders.add(sortDirection == SortDirection.asc ? indexInfo.indexName.asc() : indexInfo.indexName.desc());
        } else if ("employedItemsCount".equals(sortBy)) {
            orders.add(sortDirection == SortDirection.asc ? indexInfo.employedItemsCount.asc() : indexInfo.employedItemsCount.desc());
        }

        // 항상 id 정렬 포함하여 안정적인 정렬 보장
        orders.add(sortDirection == SortDirection.asc ? indexInfo.id.asc() : indexInfo.id.desc());

        return orders;
    }

    @Override
    public long countByConditions(String indexClassification, String indexName, Boolean favorite) {
        QIndexInfo indexInfo = QIndexInfo.indexInfo;

        BooleanBuilder filters = new BooleanBuilder();
        if (indexClassification != null && !indexClassification.isBlank()) {
            filters.and(indexInfo.indexClassification.containsIgnoreCase(indexClassification));
        }
        if (indexName != null && !indexName.isBlank()) {
            filters.and(indexInfo.indexName.containsIgnoreCase(indexName));
        }
        if (favorite != null) {
            filters.and(indexInfo.favorite.eq(favorite));
        }

        Long cnt = queryFactory
                .select(indexInfo.count())
                .from(indexInfo)
                .where(filters)
                .fetchOne();

        return cnt != null ? cnt : 0L;
    }
}