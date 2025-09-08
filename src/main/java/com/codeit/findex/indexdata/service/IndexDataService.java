package com.codeit.findex.indexdata.service;

import com.codeit.findex.common.model.SourceType;
import com.codeit.findex.common.paging.CursorPageResponse;
import com.codeit.findex.indexdata.controller.PeriodType;
import com.codeit.findex.indexdata.dto.*;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexdata.repository.IndexDataRepositoryCustom.SortKey;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class IndexDataService {

  private final IndexDataRepository repo;
  private final IndexInfoRepository indexRepo;
  private final IndexDataMapper mapper;

  public CursorPageResponse<IndexDataDto> list(
      Long indexInfoId, java.time.LocalDate startDate, java.time.LocalDate endDate,
      SortKey sort, boolean asc, Long prevLastId, int size
  ) {
    requireIndex(indexInfoId);
    return repo.search(indexInfoId, startDate, endDate, sort, asc, prevLastId, size);
  }

  public IndexDataDto create(IndexDataCreateRequest r) {
    IndexInfo index = requireIndex(r.indexInfoId());
    repo.findByIndexInfo_IdAndBaseDate(r.indexInfoId(), r.baseDate()).ifPresent(x -> {
      throw new IllegalArgumentException("이미 존재하는 (indexInfoId, baseDate) 입니다.");
    });

    IndexData saved = repo.save(IndexData.builder()
        .indexInfo(index)
        .baseDate(r.baseDate())
        .sourceType(parseSourceType(r.sourceType()))
        .marketPrice(r.marketPrice())
        .closingPrice(r.closingPrice())
        .highPrice(r.highPrice())
        .lowPrice(r.lowPrice())
        .versus(r.versus())
        .fluctuationRate(r.fluctuationRate())
        .tradingQuantity(r.tradingQuantity())
        .tradingPrice(r.tradingPrice())
        .marketTotalAmount(r.marketTotalAmount())
        .build());
    return mapper.toDto(saved);
  }

  @Transactional(readOnly = true)
  public byte[] exportCsv(Long indexInfoId, java.time.LocalDate startDate,
      java.time.LocalDate endDate, SortKey sort, boolean asc) {

    requireIndex(indexInfoId);

    final int CHUNK = 1000;
    Long cursor = null;
    boolean hasNext;
    List<IndexDataDto> all = new ArrayList<>();

    do {
      var page = repo.search(indexInfoId, startDate, endDate, sort, asc, cursor, CHUNK);
      all.addAll(page.content());
      hasNext = page.hasNext();
      cursor = page.lastId();
    } while (hasNext);

    StringBuilder sb = new StringBuilder();
    sb.append('\uFEFF');
    sb.append("id,indexInfoId,indexName,baseDate,sourceType,")
        .append("marketPrice,closingPrice,highPrice,lowPrice,")
        .append("versus,fluctuationRate,tradingQuantity,tradingPrice,marketTotalAmount\n");

    for (IndexDataDto d : all) {
      sb.append(n(d.id())).append(',')
          .append(n(d.indexInfoId())).append(',')
          .append(esc(d.indexName())).append(',')
          .append(esc(d.baseDate() == null ? null : d.baseDate().toString())).append(',')
          .append(esc(d.sourceType())).append(',')
          .append(n(d.marketPrice())).append(',')
          .append(n(d.closingPrice())).append(',')
          .append(n(d.highPrice())).append(',')
          .append(n(d.lowPrice())).append(',')
          .append(n(d.versus())).append(',')
          .append(n(d.fluctuationRate())).append(',')
          .append(n(d.tradingQuantity())).append(',')
          .append(n(d.tradingPrice())).append(',')
          .append(n(d.marketTotalAmount())).append('\n');
    }
    return sb.toString().getBytes(StandardCharsets.UTF_8);
  }

  public void delete(Long id) { repo.deleteById(id); }

  public IndexDataDto update(Long id, IndexDataUpdateRequest r) {
    IndexData d = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("데이터가 없습니다."));
    if (r.sourceType()        != null) d.setSourceType(parseSourceType(r.sourceType()));
    if (r.marketPrice()       != null) d.setMarketPrice(r.marketPrice());
    if (r.closingPrice()      != null) d.setClosingPrice(r.closingPrice());
    if (r.highPrice()         != null) d.setHighPrice(r.highPrice());
    if (r.lowPrice()          != null) d.setLowPrice(r.lowPrice());
    if (r.versus()            != null) d.setVersus(r.versus());
    if (r.fluctuationRate()   != null) d.setFluctuationRate(r.fluctuationRate());
    if (r.tradingQuantity()   != null) d.setTradingQuantity(r.tradingQuantity());
    if (r.tradingPrice()      != null) d.setTradingPrice(r.tradingPrice());
    if (r.marketTotalAmount() != null) d.setMarketTotalAmount(r.marketTotalAmount());
    return mapper.toDto(d);
  }

  @Transactional(readOnly = true)
  public IndexChartDto chart(Long indexDataId) {
    IndexData base = repo.findById(indexDataId).orElseThrow(() -> new IllegalArgumentException("데이터가 없습니다."));
    Long indexInfoId = base.getIndexInfo().getId();
    var to = base.getBaseDate();
    var from = to.minusMonths(6);

    var rows = repo.findTop60ByIndexInfo_IdAndBaseDateBetweenOrderByBaseDateAsc(indexInfoId, from, to);
    List<ChartPoint> series = new ArrayList<>();
    var closes = new ArrayList<Double>();

    for (IndexData r : rows) {
      Double c = Optional.ofNullable(r.getClosingPrice()).orElse(0.0);
      closes.add(c);
      series.add(new ChartPoint(r.getBaseDate(), r.getClosingPrice(),
          avgTail(closes, 5), avgTail(closes, 20)));
    }
    return new IndexChartDto(indexInfoId, base.getIndexInfo().getIndexName(), series);
  }

  @Transactional(readOnly = true)
  public List<IndexPerformanceDto> favoritePerformance(PeriodType from) {
    var favorites = indexRepo.findByFavoriteTrue();
    var today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    List<IndexPerformanceDto> out = new ArrayList<>();
    for (IndexInfo idx : favorites) out.add(perf(idx.getId(), idx.getIndexName(), today));
    return out;
  }

  @Transactional(readOnly = true)
  public List<IndexPerformanceDto> rankPerformance() {
    var all = indexRepo.findAll();
    var today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    List<IndexPerformanceDto> perf = new ArrayList<>();
    for (IndexInfo idx : all) perf.add(perf(idx.getId(), idx.getIndexName(), today));
    perf.sort(Comparator.comparing((IndexPerformanceDto p) -> nullSafe(p.d1ChangePct())).reversed());
    return perf.stream().limit(10).toList();
  }

  private Double nullSafe(Double d) { return d == null ? -9e9 : d; }

  private IndexPerformanceDto perf(Long indexInfoId, String indexName, LocalDate base) {
    var today = repo.findTop1ByIndexInfo_IdAndBaseDateLessThanEqualOrderByBaseDateDesc(indexInfoId, base).orElse(null);
    var d1    = repo.findTop1ByIndexInfo_IdAndBaseDateLessThanEqualOrderByBaseDateDesc(indexInfoId, base.minusDays(1)).orElse(null);
    var w1    = repo.findTop1ByIndexInfo_IdAndBaseDateLessThanEqualOrderByBaseDateDesc(indexInfoId, base.minusWeeks(1)).orElse(null);
    var m1    = repo.findTop1ByIndexInfo_IdAndBaseDateLessThanEqualOrderByBaseDateDesc(indexInfoId, base.minusMonths(1)).orElse(null);

    Double close = today == null ? null : today.getClosingPrice();
    return new IndexPerformanceDto(indexInfoId, indexName, close,
        pct(today, d1), pct(today, w1), pct(today, m1));
  }

  private Double pct(IndexData a, IndexData b) {
    if (a == null || b == null || b.getClosingPrice() == null || b.getClosingPrice() == 0) return null;
    return (a.getClosingPrice() - b.getClosingPrice()) / b.getClosingPrice() * 100.0;
  }

  private Double avgTail(List<Double> list, int n) {
    if (list.isEmpty()) return null;
    int from = Math.max(0, list.size()-n);
    return list.subList(from, list.size()).stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
  }

  private IndexInfo requireIndex(Long id) {
    return indexRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지수입니다."));
  }

  private SourceType parseSourceType(String v) {
    if (v == null || v.isBlank()) return SourceType.OPEN_API;
    return SourceType.valueOf(v.trim().toUpperCase(java.util.Locale.ROOT));
  }

  private String n(Number v) { return v == null ? "" : v.toString(); }
  private String esc(String s) {
    if (s == null) return "";
    if (s.contains(",") || s.contains("\"") || s.contains("\n"))
      return "\"" + s.replace("\"", "\"\"") + "\"";
    return s;
  }
}