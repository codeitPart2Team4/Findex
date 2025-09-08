package com.codeit.findex.indexdata.controller;

import com.codeit.findex.common.paging.CursorPageResponse;
import com.codeit.findex.indexdata.dto.*;
import com.codeit.findex.indexdata.repository.IndexDataRepositoryCustom.SortKey;
import com.codeit.findex.indexdata.service.IndexDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataController {

  private final IndexDataService service;
  private static final int DEFAULT_SIZE = 10;

  // 목록 조회(필터·정렬·커서)
  @GetMapping
  public CursorPageResponse<IndexDataDto> list(
      @RequestParam Long indexInfoId,
      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required=false) Long idAfter,
      @RequestParam(required=false) String cursor,
      @RequestParam(defaultValue = "baseDate") String sortField,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(required=false) Integer size
  ) {
    SortKey sortKey = mapSortKey(SortField.from(sortField));
    boolean asc     = (SortDirection.from(sortDirection) == SortDirection.asc);
    int pageSize    = (size == null ? DEFAULT_SIZE : size);

    Long prevLastId = decodeCursorToId(cursor);
    if (prevLastId == null) prevLastId = idAfter;

    return service.list(indexInfoId, startDate, endDate, sortKey, asc, prevLastId, pageSize);
  }

  // 등록
  @PostMapping
  public ResponseEntity<IndexDataDto> create(@RequestBody IndexDataCreateRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
  }

  // 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  // 수정(PATCH)
  @PatchMapping("/{id}")
  public IndexDataDto update(@PathVariable Long id, @RequestBody IndexDataUpdateRequest req) {
    return service.update(id, req);
  }

  // 차트
  @GetMapping("/{id}/chart")
  public IndexChartDto chart(@PathVariable Long id,
      @RequestParam(defaultValue = "DAILY") String periodType) {
    return service.chart(id); // 현재 서비스는 periodType 미사용
  }

  // 성과 랭킹
  @GetMapping("/performance/rank")
  public List<IndexPerformanceWithRankDto> rank(
      @RequestParam(required=false) Long indexInfoId,
      @RequestParam(defaultValue = "DAILY") String periodType,
      @RequestParam(defaultValue = "10") Integer limit
  ) {
    var list = service.rankPerformance();
    List<IndexPerformanceWithRankDto> out = new ArrayList<>();
    int r = 1;
    for (var p : list) {
      out.add(new IndexPerformanceWithRankDto(p, r++));
      if (limit != null && r > limit) break;
    }
    return out;
  }

  // 즐겨찾기 성과
  @GetMapping("/performance/favorite")
  public List<IndexPerformanceDto> favorite(
      @RequestParam(defaultValue = "DAILY") String periodType
  ) {
    return service.favoritePerformance(PeriodType.from(periodType));
  }

  // CSV export
  @GetMapping(value="/export/csv", produces="text/csv")
  public ResponseEntity<byte[]> export(
      @RequestParam Long indexInfoId,
      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(defaultValue = "baseDate") String sortField,
      @RequestParam(defaultValue = "desc") String sortDirection
  ) {
    SortKey sortKey = mapSortKey(SortField.from(sortField));
    boolean asc     = (SortDirection.from(sortDirection) == SortDirection.asc);

    byte[] csv = service.exportCsv(indexInfoId, startDate, endDate, sortKey, asc);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"index-data.csv\"")
        .contentType(MediaType.valueOf("text/csv"))
        .body(csv);
  }

  // ---- helpers ------------------------------------------------------------

  private SortKey mapSortKey(SortField f) {
    return switch (f) {
      case baseDate        -> SortKey.DATE;
      case closingPrice    -> SortKey.CLOSE;
      case tradingQuantity -> SortKey.VOLUME;
      case tradingPrice    -> SortKey.TURNOVER;
      default              -> SortKey.DATE; // 나머지는 DATE로 폴백
    };
  }

  private Long decodeCursorToId(String cursor) {
    if (cursor == null || cursor.isBlank()) return null;
    try {
      String json = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
      int i = json.indexOf(':'); int j = json.indexOf('}', i+1);
      String num = (i>0 && j>i) ? json.substring(i+1, j).replaceAll("[^0-9]", "") : "";
      return num.isEmpty() ? null : Long.parseLong(num);
    } catch (Exception e) { return null; }
  }
}
