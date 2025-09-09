package com.codeit.findex.indexdata.service;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.codeit.findex.common.dto.PageResponse;
import com.codeit.findex.indexdata.controller.PeriodType;
import com.codeit.findex.indexdata.controller.SortField;
import com.codeit.findex.indexdata.dto.ChartPoint;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.dto.IndexPerformanceWithRankDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.indexinfo.repository.IndexInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataService {

  private final IndexDataRepository indexDataRepository;
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataMapper indexDataMapper;

  private static final ObjectMapper OM = new ObjectMapper();
  private static final Encoder B64E = Base64.getUrlEncoder().withoutPadding();
  private static final Decoder B64D = Base64.getUrlDecoder();

  // ========== 목록 ==========
  public PageResponse<IndexDataDto> list(
      Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate,
      String cursor,
      Long idAfter,
      SortField sortField,
      String sortDirection,
      Integer size
  ) {
    int pageSize = (size == null || size <= 0) ? 10 : Math.min(size, 200);
    boolean desc = !"asc".equalsIgnoreCase(sortDirection);

    // 커서 복원
    Long pivotId = null;
    Double pivotSortVal = null;
    if (StringUtils.hasText(cursor)) {
      Map<String, Object> m = decodeCursor(cursor);
      if (m != null) {
        Object id = m.get("id");
        Object lv = m.get("lastSortValue");
        if (id instanceof Number n) pivotId = n.longValue();
        if (lv instanceof Number n) pivotSortVal = n.doubleValue();
      }
    } else if (idAfter != null) {
      pivotId = idAfter;
    }

    // baseDate를 정렬 키로 쓸 때 pivotSortVal 준비
    if (pivotId != null && pivotSortVal == null && sortField == SortField.baseDate) {
      IndexData last = indexDataRepository.findById(pivotId).orElse(null);
      if (last != null && last.getBaseDate() != null) {
        pivotSortVal = (double) last.getBaseDate().toEpochDay();
      }
    }

    List<IndexData> rows = indexDataRepository.findForList(
        indexInfoId, startDate, endDate, sortField, desc, pivotSortVal, pivotId, pageSize + 1
    );

    boolean hasNext = rows.size() > pageSize;
    if (hasNext) rows = rows.subList(0, pageSize);

    List<IndexDataDto> content = rows.stream().map(indexDataMapper::toDto).toList();

    String nextCursor = null;
    if (hasNext && !rows.isEmpty()) {
      IndexData last = rows.get(rows.size() - 1);
      Double lastVal = extractSortValue(last, sortField);
      Map<String, Object> cur = new HashMap<>();
      cur.put("id", last.getId());
      cur.put("lastSortValue", lastVal);
      cur.put("sortField", sortField.name());
      cur.put("sortDirection", desc ? "desc" : "asc");
      nextCursor = encodeCursor(cur);
    }

    long total = indexDataRepository.countForList(indexInfoId, startDate, endDate);
    return new PageResponse<>(content, nextCursor, pageSize, total, hasNext);
  }

  private Double extractSortValue(IndexData d, SortField f) {
    return switch (f) {
      case baseDate -> d.getBaseDate() == null ? null : (double) d.getBaseDate().toEpochDay();
      case marketPrice -> d.getMarketPrice();
      case closingPrice -> d.getClosingPrice();
      case highPrice -> d.getHighPrice();
      case lowPrice -> d.getLowPrice();
      case versus -> d.getVersus();
      case fluctuationRate -> d.getFluctuationRate();
      case tradingQuantity -> d.getTradingQuantity() == null ? null : d.getTradingQuantity().doubleValue();
      case tradingPrice -> d.getTradingPrice() == null ? null : d.getTradingPrice().doubleValue();
      case marketTotalAmount -> d.getMarketTotalAmount() == null ? null : d.getMarketTotalAmount().doubleValue();
    };
  }

  private String encodeCursor(Map<String, Object> payload) {
    try {
      String json = OM.writeValueAsString(payload);
      return B64E.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> decodeCursor(String cur) {
    try {
      byte[] bytes = B64D.decode(cur);
      return OM.readValue(bytes, Map.class);
    } catch (Exception e) {
      return null;
    }
  }

  // ========== 생성 ==========
  @Transactional
  public IndexDataDto create(IndexDataCreateRequest req) {
    IndexInfo info = indexInfoRepository.findById(req.indexInfoId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "IndexInfo not found"));

    // (indexInfoId, baseDate) 중복 방지 → 409
    boolean dup = !indexDataRepository.findForList(
        req.indexInfoId(),          // index
        req.baseDate(),             // start = baseDate
        req.baseDate(),             // end   = baseDate
        SortField.baseDate,
        false,
        null,
        null,
        1
    ).isEmpty();
    if (dup) {
      throw new ResponseStatusException(CONFLICT, "Duplicate (indexInfoId, baseDate)");
    }

    IndexData ent = indexDataMapper.fromCreate(req);
    ent.setIndexInfo(info);
    if (ent.getSourceType() == null && info.getSourceType() != null) {
      ent.setSourceType(info.getSourceType());
    }
    IndexData saved = indexDataRepository.save(ent);
    return indexDataMapper.toDto(saved);
  }

  // ===== 수정 =====
  @Transactional
  public IndexDataDto update(Long id, IndexDataUpdateRequest req) {
    IndexData d = indexDataRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "IndexData not found"));

    indexDataMapper.patch(d, req);
    return indexDataMapper.toDto(d);
  }

  // ===== 삭제 =====
  @Transactional
  public void delete(Long id) {
    IndexData d = indexDataRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "IndexData not found"));
    indexDataRepository.delete(d);
  }

  // ========== 차트 ==========
  public IndexChartDto chart(Long indexInfoId, PeriodType periodType) {
    //  404 매핑
    IndexInfo info = indexInfoRepository.findById(indexInfoId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "IndexInfo not found"));

    LocalDate to = LocalDate.now();
    LocalDate from = switch (periodType) {
      case DAILY -> to.minusMonths(6);
      case WEEKLY -> to.minusMonths(12);
      case MONTHLY -> to.minusYears(3);
      case QUARTERLY -> to.minusYears(5);
      case YEARLY -> to.minusYears(10);
    };

    // 기간 내 데이터(오름차순)
    List<IndexData> list = indexDataRepository.findForList(
        indexInfoId, from, to, SortField.baseDate, false, null, null, Integer.MAX_VALUE
    );

    // 버킷 마지막 종가를 value로 사용
    Map<LocalDate, Double> bucketed = aggregateByPeriod(list, periodType);
    List<ChartPoint> data = bucketed.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(e -> new ChartPoint(e.getKey(), e.getValue()))
        .toList();

    List<ChartPoint> ma5  = movingAverage(data, 5);
    List<ChartPoint> ma20 = movingAverage(data, 20);

    return new IndexChartDto(
        info.getId(),
        info.getIndexClassification(),
        info.getIndexName(),
        periodType.name(),
        data,
        ma5,
        ma20
    );
  }

  private Map<LocalDate, Double> aggregateByPeriod(List<IndexData> all, PeriodType p) {
    Map<String, IndexData> lastOfBucket = new HashMap<>();
    WeekFields wf = WeekFields.ISO;
    for (IndexData d : all) {
      if (d.getBaseDate() == null) continue;
      String key = switch (p) {
        case DAILY     -> d.getBaseDate().toString();
        case WEEKLY    -> d.getBaseDate().getYear() + "-W" + d.getBaseDate().get(wf.weekOfWeekBasedYear());
        case MONTHLY   -> d.getBaseDate().getYear() + "-" + String.format("%02d", d.getBaseDate().getMonthValue());
        case QUARTERLY -> d.getBaseDate().getYear() + "-Q" + ((d.getBaseDate().getMonthValue() - 1) / 3 + 1);
        case YEARLY    -> String.valueOf(d.getBaseDate().getYear());
      };
      IndexData prev = lastOfBucket.get(key);
      if (prev == null || prev.getBaseDate().isBefore(d.getBaseDate())) {
        lastOfBucket.put(key, d);
      }
    }
    Map<LocalDate, Double> out = new HashMap<>();
    for (Map.Entry<String, IndexData> e : lastOfBucket.entrySet()) {
      IndexData v = e.getValue();
      LocalDate date = switch (p) {
        case DAILY     -> v.getBaseDate();
        case WEEKLY    -> v.getBaseDate().with(wf.dayOfWeek(), 7);
        case MONTHLY   -> v.getBaseDate().withDayOfMonth(v.getBaseDate().lengthOfMonth());
        case QUARTERLY -> {
          int q = (v.getBaseDate().getMonthValue() - 1) / 3 + 1;
          int endMonth = q * 3;
          LocalDate end = LocalDate.of(v.getBaseDate().getYear(), endMonth, 1)
              .withDayOfMonth(LocalDate.of(v.getBaseDate().getYear(), endMonth, 1).lengthOfMonth());
          yield end;
        }
        case YEARLY    -> LocalDate.of(v.getBaseDate().getYear(), 12, 31);
      };
      out.put(date, v.getClosingPrice());
    }
    return out;
  }

  private List<ChartPoint> movingAverage(List<ChartPoint> data, int window) {
    List<ChartPoint> out = new ArrayList<>();
    Deque<Double> q = new ArrayDeque<>();
    double sum = 0.0;
    for (int i = 0; i < data.size(); i++) {
      Double val = data.get(i).value();
      if (val == null) val = 0.0;
      q.addLast(val); sum += val;
      if (q.size() > window) sum -= q.removeFirst();
      if (q.size() == window) out.add(new ChartPoint(data.get(i).date(), sum / window));
      else out.add(new ChartPoint(data.get(i).date(), null));
    }
    return out;
  }

  // ========== 성과 랭킹 ==========
  public List<IndexPerformanceWithRankDto> rank(Long indexInfoId, PeriodType periodType, Integer limit) {
    int lim = (limit == null || limit <= 0) ? 10 : Math.min(limit, 100);
    LocalDate today = LocalDate.now();
    LocalDate before = switch (periodType) {
      case DAILY -> today.minusDays(1);
      case WEEKLY -> today.minusWeeks(1);
      case MONTHLY -> today.minusMonths(1);
      default -> today.minusDays(1); // 스펙은 3종
    };

    // 최근/비교 시점 각각에서 IndexInfo별 최신 종가 선택
    List<IndexData> current = indexDataRepository.findForList(
        indexInfoId, today.minusMonths(3), today, SortField.baseDate, true, null, null, Integer.MAX_VALUE);
    List<IndexData> earlier = indexDataRepository.findForList(
        indexInfoId, before.minusMonths(3), before, SortField.baseDate, true, null, null, Integer.MAX_VALUE);

    Map<Long, IndexData> curMap = pickLatestByInfo(current);
    Map<Long, IndexData> befMap = pickLatestBeforeByInfo(earlier, before);

    List<IndexPerformanceDto> perf = new ArrayList<>();
    for (Map.Entry<Long, IndexData> e : curMap.entrySet()) {
      Long infoId = e.getKey();
      IndexData now = e.getValue();
      IndexData bef = befMap.get(infoId);
      if (now == null || bef == null) continue;

      double currentPrice = of(now.getClosingPrice());
      double beforePrice  = of(bef.getClosingPrice());
      double versus = currentPrice - beforePrice;
      double fluc = (beforePrice == 0.0) ? 0.0 : (versus / beforePrice) * 100.0;

      IndexInfo info = now.getIndexInfo();
      perf.add(new IndexPerformanceDto(
          infoId,
          info.getIndexClassification(),
          info.getIndexName(),
          versus,
          fluc,
          currentPrice,
          beforePrice
      ));
    }

    // fluctuationRate desc, tie → versus desc
    perf.sort(Comparator
        .comparing(IndexPerformanceDto::fluctuationRate).reversed()
        .thenComparing(IndexPerformanceDto::versus).reversed());

    List<IndexPerformanceWithRankDto> out = new ArrayList<>();
    for (int i = 0; i < Math.min(perf.size(), lim); i++) {
      out.add(new IndexPerformanceWithRankDto(perf.get(i), i + 1));
    }
    return out;
  }

  private Map<Long, IndexData> pickLatestByInfo(List<IndexData> list) {
    Map<Long, IndexData> m = new HashMap<>();
    for (IndexData d : list) {
      Long id = d.getIndexInfo().getId();
      IndexData prev = m.get(id);
      if (prev == null || prev.getBaseDate().isBefore(d.getBaseDate())) m.put(id, d);
    }
    return m;
  }

  private Map<Long, IndexData> pickLatestBeforeByInfo(List<IndexData> list, LocalDate before) {
    Map<Long, IndexData> m = new HashMap<>();
    for (IndexData d : list) {
      if (d.getBaseDate() == null || d.getBaseDate().isAfter(before)) continue;
      Long id = d.getIndexInfo().getId();
      IndexData prev = m.get(id);
      if (prev == null || prev.getBaseDate().isBefore(d.getBaseDate())) m.put(id, d);
    }
    return m;
  }

  private double of(Double v) { return v == null ? 0.0 : v; }

  // ========== 관심 성과 ==========
  public List<IndexPerformanceDto> favorite(PeriodType periodType) {
    // 1) 전체 성과 산출
    List<IndexPerformanceDto> all = rank(null, periodType, Integer.MAX_VALUE).stream()
        .map(IndexPerformanceWithRankDto::performance)
        .toList();
    // 2) 즐겨찾기 인덱스만 필터
    Set<Long> favIds = indexInfoRepository.findAll().stream()
        .filter(IndexInfo::getFavorite) // 엔티티에 맞게 getFavorite()/isFavorite() 사용
        .map(IndexInfo::getId)
        .collect(java.util.stream.Collectors.toSet());
    return all.stream()
        .filter(p -> favIds.contains(p.indexInfoId()))
        .toList();
  }

  // ========== CSV ==========
  public byte[] exportCsv(Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate,
      SortField sortField,
      String sortDirection) {
    boolean desc = !"asc".equalsIgnoreCase(sortDirection);
    List<IndexData> list = indexDataRepository.findForList(
        indexInfoId, startDate, endDate, sortField, desc, null, null, Integer.MAX_VALUE
    );
    StringBuilder sb = new StringBuilder();
    sb.append("id,indexInfoId,baseDate,sourceType,marketPrice,closingPrice,highPrice,lowPrice,versus,fluctuationRate,tradingQuantity,tradingPrice,marketTotalAmount\n");
    for (IndexData d : list) {
      sb.append(d.getId()).append(',')
          .append(d.getIndexInfo().getId()).append(',')
          .append(d.getBaseDate()).append(',')
          .append(d.getSourceType()).append(',')
          .append(n(d.getMarketPrice())).append(',')
          .append(n(d.getClosingPrice())).append(',')
          .append(n(d.getHighPrice())).append(',')
          .append(n(d.getLowPrice())).append(',')
          .append(n(d.getVersus())).append(',')
          .append(n(d.getFluctuationRate())).append(',')
          .append(n(d.getTradingQuantity())).append(',')
          .append(n(d.getTradingPrice())).append(',')
          .append(n(d.getMarketTotalAmount()))
          .append('\n');
    }
    return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
  }

  private String n(Number v) { return v == null ? "" : String.valueOf(v); }
}
