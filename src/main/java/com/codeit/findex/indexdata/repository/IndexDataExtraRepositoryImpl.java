package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.dto.ChartPointDto;
import com.codeit.findex.indexdata.dto.PerformanceRankDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IndexDataExtraRepositoryImpl implements IndexDataExtraRepository {

  @PersistenceContext
  private final EntityManager em;

  @Override
  public List<ChartPointDto> findChartPoints(Long indexInfoId, LocalDate start, LocalDate end) {
    var jpql = """
            select new com.codeit.findex.indexdata.dto.ChartPointDto(
                d.baseDate, d.marketPrice, d.closingPrice, d.highPrice, d.lowPrice, d.tradingQuantity
            )
            from IndexData d
            where d.indexInfo.id = :iid
              and d.baseDate between :s and :e
            order by d.baseDate asc
            """;
    return em.createQuery(jpql, ChartPointDto.class)
        .setParameter("iid", indexInfoId)
        .setParameter("s", start)
        .setParameter("e", end)
        .getResultList();
  }

  @Override
  public List<PerformanceRankDto> findPerformanceRank(LocalDate start, LocalDate end, boolean favoriteOnly, int size) {
    // Postgres Native SQL (기간 내 첫/마지막 종가로 수익률 계산)
    var sql = """
            with base as (
                select distinct d.index_info_id
                from index_data d
                where d.base_date between :s and :e
            ),
            fl as (
                select b.index_info_id,
                       (select d2.closing_price from index_data d2
                         where d2.index_info_id = b.index_info_id
                           and d2.base_date between :s and :e
                         order by d2.base_date asc limit 1) as first_close,
                       (select d3.closing_price from index_data d3
                         where d3.index_info_id = b.index_info_id
                           and d3.base_date between :s and :e
                         order by d3.base_date desc limit 1) as last_close
                from base b
            )
            select ii.id, ii.index_name, ii.index_classification,
                   fl.first_close, fl.last_close,
                   (fl.last_close - fl.first_close) as abs_change,
                   case when fl.first_close > 0
                        then ((fl.last_close - fl.first_close)/fl.first_close*100.0) end as return_pct
            from fl
            join index_info ii on ii.id = fl.index_info_id
            where (:fav = false or ii.favorite = true)
            order by return_pct desc nulls last
            limit :lim
            """;
    @SuppressWarnings("unchecked")
    List<Object[]> rows = em.createNativeQuery(sql)
        .setParameter("s", start)
        .setParameter("e", end)
        .setParameter("fav", favoriteOnly)
        .setParameter("lim", size)
        .getResultList();

    List<PerformanceRankDto> list = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      list.add(PerformanceRankDto.builder()
          .indexInfoId(((Number) r[0]).longValue())
          .indexName((String) r[1])
          .indexClassification((String) r[2])
          .firstClose(r[3]==null?null:((Number) r[3]).doubleValue())
          .lastClose (r[4]==null?null:((Number) r[4]).doubleValue())
          .absChange (r[5]==null?null:((Number) r[5]).doubleValue())
          .returnPct (r[6]==null?null:((Number) r[6]).doubleValue())
          .build());
    }
    return list;
  }

  @Override
  public List<Object[]> findForCsv(Long indexInfoId, LocalDate start, LocalDate end) {
    var sql = """
            select d.base_date, d.market_price, d.closing_price, d.high_price, d.low_price,
                   d.versus, d.fluctuation_rate, d.trading_quantity, d.trading_price,
                   d.market_total_amount, d.source_type
            from index_data d
            where d.index_info_id = :iid
              and d.base_date between :s and :e
            order by d.base_date asc, d.id asc
            """;
    return em.createNativeQuery(sql)
        .setParameter("iid", indexInfoId)
        .setParameter("s", start)
        .setParameter("e", end)
        .getResultList();
  }
}
