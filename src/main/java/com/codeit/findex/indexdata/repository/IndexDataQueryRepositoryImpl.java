package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.controller.SortField;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.entity.QIndexData;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.dsl.Expressions;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IndexDataQueryRepositoryImpl implements IndexDataQueryRepository {

  private final EntityManager em;

  private JPAQueryFactory qf() {
    return new JPAQueryFactory(em);
  }

  @Override
  public List<IndexData> findForList(Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate,
      SortField sortField,
      boolean desc,
      Double pivotSortVal,
      Long pivotId,
      int limitPlusOne) {
    QIndexData d = QIndexData.indexData;
    BooleanBuilder where = new BooleanBuilder();

    if (indexInfoId != null)
      where.and(d.indexInfo.id.eq(indexInfoId));
    if (startDate != null)
      where.and(d.baseDate.goe(startDate));
    if (endDate != null)
      where.and(d.baseDate.loe(endDate));

    // 커서 조건
    if (pivotId != null) {
      ComparableExpressionBase<?> sortExpr = pickExpr(d, sortField); // 정렬 값 추출
      BooleanExpression tieId;

      if (desc) {
        BooleanExpression lessSort = cmpLess(sortExpr, pivotSortVal);
        BooleanExpression eqSort = cmpEq(sortExpr, pivotSortVal);
        tieId = lessSort
            .or(eqSort.and(d.id.lt(pivotId)));
      } else {
        BooleanExpression greaterSort = cmpGreater(sortExpr, pivotSortVal);
        BooleanExpression eqSort = cmpEq(sortExpr, pivotSortVal);
        tieId = greaterSort
            .or(eqSort.and(d.id.gt(pivotId)));
      }
      where.and(tieId);
    }

    OrderSpecifier<?>[] orderSpec = orderBy(d, sortField, desc);
    return qf().selectFrom(d)
        .where(where)
        .orderBy(orderSpec)
        .limit(limitPlusOne)
        .fetch();
  }

  @Override
  public long countForList(Long indexInfoId, LocalDate startDate, LocalDate endDate) {
    QIndexData d = QIndexData.indexData;
    BooleanBuilder where = new BooleanBuilder();
    if (indexInfoId != null)
      where.and(d.indexInfo.id.eq(indexInfoId));
    if (startDate != null)
      where.and(d.baseDate.goe(startDate));
    if (endDate != null)
      where.and(d.baseDate.loe(endDate));
    Long cnt = qf().select(d.count()).from(d).where(where).fetchOne();
    return cnt == null ? 0L : cnt;
  }

  // --- helpers ---
  private ComparableExpressionBase<?> pickExpr(QIndexData d, SortField f) {
    return switch (f) {
      case baseDate -> d.baseDate;
      case marketPrice -> d.marketPrice;
      case closingPrice -> d.closingPrice;
      case highPrice -> d.highPrice;
      case lowPrice -> d.lowPrice;
      case versus -> d.versus;
      case fluctuationRate -> d.fluctuationRate;
      case tradingQuantity -> d.tradingQuantity;
      case tradingPrice -> d.tradingPrice;
      case marketTotalAmount -> d.marketTotalAmount;
    };
  }

  private OrderSpecifier<?>[] orderBy(QIndexData d, SortField f, boolean desc) {
    Order dir = desc ? Order.DESC : Order.ASC;
    ComparableExpressionBase<?> expr = pickExpr(d, f);
    OrderSpecifier<?> primary = new OrderSpecifier<>(dir, expr).nullsLast();
    return new OrderSpecifier<?>[]{
        primary,
        new OrderSpecifier<>(dir, d.id)
    };
  }

  private BooleanExpression cmpEq(ComparableExpressionBase<?> expr, Double val) {
    if (val == null)
      return Expressions.FALSE.isTrue();
    if (expr instanceof DatePath<?> date) {
      return Expressions.FALSE.isTrue();
    }
    if (expr instanceof NumberExpression<?> num) {
      return Expressions.booleanTemplate("{0} = {1}", num, val);
    }
    return Expressions.FALSE.isTrue();
  }

  private BooleanExpression cmpLess(ComparableExpressionBase<?> expr, Double val) {
    if (val == null)
      return Expressions.FALSE.isTrue();

    if (expr instanceof DatePath<?> date) {
      LocalDate pivot = LocalDate.ofEpochDay(val.longValue());
      @SuppressWarnings("unchecked")
      DatePath<LocalDate> dp = (DatePath<LocalDate>) date;
      return dp.lt(pivot);
    }
    if (expr instanceof NumberExpression<?> num) {
      return num.lt(val);
    }
    return Expressions.FALSE.isTrue();
  }

  private BooleanExpression cmpGreater(ComparableExpressionBase<?> expr, Double val) {
    if (val == null)
      return Expressions.FALSE.isTrue();

    if (expr instanceof DatePath<?> date) {
      LocalDate pivot = LocalDate.ofEpochDay(val.longValue());
      @SuppressWarnings("unchecked")
      DatePath<LocalDate> dp = (DatePath<LocalDate>) date;
      return dp.gt(pivot);
    }
    if (expr instanceof NumberExpression<?> num) {
      return num.gt(val);
    }
    return Expressions.FALSE.isTrue();
  }
}