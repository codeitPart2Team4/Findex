package com.codeit.findex.indexdata.controller;

import com.codeit.findex.indexdata.dto.*;
import com.codeit.findex.indexdata.service.IndexDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
@Tag(name = "index-data-extra-controller")
public class IndexDataExtraController implements IndexDataExtraApi {

  private final IndexDataService service;

  // ===== 차트 =====
  @GetMapping("/{id}/chart")
  @Operation(summary = "지수 차트 조회")
  public IndexChartDto chart(
      @PathVariable("id") Long indexInfoId,
      @RequestParam(defaultValue = "DAILY") String periodType
  ) {
    return service.chart(indexInfoId, PeriodType.from(periodType));
  }

  // ===== 성과 랭킹 =====
  @GetMapping("/performance/rank")
  @Operation(summary = "지수 성과 랭킹 조회")
  public List<IndexPerformanceWithRankDto> rank(
      @RequestParam(required = false) Long indexInfoId,
      @RequestParam(defaultValue = "DAILY") String periodType,
      @RequestParam(defaultValue = "10") Integer limit
  ) {
    return service.rank(indexInfoId, PeriodType.from(periodType), limit);
  }

  // ===== 관심 성과 =====
  @GetMapping("/performance/favorite")
  @Operation(summary = "관심 지수 성과 조회")
  public List<IndexPerformanceDto> favorite(
      @RequestParam(defaultValue = "DAILY") String periodType
  ) {
    return service.favorite(PeriodType.from(periodType));
  }

  // ===== CSV Export =====
  @GetMapping(value = "/export/csv", produces = "text/csv")
  @Operation(summary = "지수 데이터 CSV export")
  public ResponseEntity<byte[]> exportCsv(
      @RequestParam(required = false) Long indexInfoId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(defaultValue = "baseDate") String sortField,
      @RequestParam(defaultValue = "desc") String sortDirection
  ) {
    byte[] csv = service.exportCsv(indexInfoId, startDate, endDate, SortField.from(sortField), sortDirection);
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"index-data.csv\"");
    headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
    return new ResponseEntity<>(csv, headers, HttpStatus.OK);
  }
}
