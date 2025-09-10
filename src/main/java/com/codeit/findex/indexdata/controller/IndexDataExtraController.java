package com.codeit.findex.indexdata.controller;

import com.codeit.findex.indexdata.dto.*;
import com.codeit.findex.indexdata.service.IndexDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
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
public class IndexDataExtraController {

  private final IndexDataService service;

  // ===== 차트 =====
  @GetMapping("/{id}/chart")
  @Operation(summary = "지수 차트 조회", description = "Path의 {id}는 IndexInfo ID 입니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK",
          content = @Content(schema = @Schema(implementation = IndexChartDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 기간 유형 등)", content = @Content),
      @ApiResponse(responseCode = "404", description = "지수 정보를 찾을 수 없음", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  public IndexChartDto chart(
      @Parameter(description = "IndexInfo ID") @PathVariable("id") Long indexInfoId,
      @Parameter(description = "차트 기간 유형",
          schema = @Schema(allowableValues = {"DAILY","WEEKLY","MONTHLY","QUARTERLY","YEARLY"}, defaultValue = "DAILY"))
      @RequestParam(defaultValue = "DAILY") String periodType
  ) {
    return service.chart(indexInfoId, PeriodType.from(periodType));
  }

  // ===== 성과 랭킹 =====
  @GetMapping("/performance/rank")
  @Operation(summary = "지수 성과 랭킹 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = IndexPerformanceWithRankDto.class)))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  public List<IndexPerformanceWithRankDto> rank(
      @Parameter(description = "지수 정보 ID") @RequestParam(required = false) Long indexInfoId,
      @Parameter(description = "성과 기간 유형", schema = @Schema(allowableValues = {"DAILY","WEEKLY","MONTHLY"}, defaultValue = "DAILY"))
      @RequestParam(defaultValue = "DAILY") String periodType,
      @Parameter(description = "최대 랭킹 수", example = "10")
      @RequestParam(defaultValue = "10") Integer limit
  ) {
    return service.rank(indexInfoId, PeriodType.from(periodType), limit);
  }

  // ===== 관심 성과 =====
  @GetMapping("/performance/favorite")
  @Operation(summary = "관심 지수 성과 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = IndexPerformanceDto.class)))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  public List<IndexPerformanceDto> favorite(
      @Parameter(description = "성과 기간 유형", schema = @Schema(allowableValues = {"DAILY","WEEKLY","MONTHLY"}, defaultValue = "DAILY"))
      @RequestParam(defaultValue = "DAILY") String periodType
  ) {
    return service.favorite(PeriodType.from(periodType));
  }

  // ===== CSV Export =====
  @GetMapping(value = "/export/csv", produces = "text/csv")
  @Operation(summary = "지수 데이터 CSV export")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "CSV 파일 생성 성공",
          content = @Content(mediaType = "text/csv",
              schema = @Schema(type = "string", format = "binary"))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  public ResponseEntity<byte[]> exportCsv(
      @Parameter(description = "지수 정보 ID") @RequestParam(required = false) Long indexInfoId,
      @Parameter(description = "시작 일자", example = "2024-01-01")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @Parameter(description = "종료 일자", example = "2024-12-31")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @Parameter(description = "정렬 필드",
          schema = @Schema(
              allowableValues = {
                  "baseDate","marketPrice","closingPrice","highPrice","lowPrice",
                  "versus","fluctuationRate","tradingQuantity","tradingPrice","marketTotalAmount"
              },
              defaultValue = "baseDate"
          )
      ) @RequestParam(defaultValue = "baseDate") String sortField,
      @Parameter(description = "정렬 방향", schema = @Schema(allowableValues = {"asc","desc"}, defaultValue = "desc"))
      @RequestParam(defaultValue = "desc") String sortDirection
  ) {
    byte[] csv = service.exportCsv(indexInfoId, startDate, endDate, SortField.from(sortField), sortDirection);
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"index-data.csv\"");
    headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
    return new ResponseEntity<>(csv, headers, HttpStatus.OK);
  }
}