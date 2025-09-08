package com.codeit.findex.indexdata.controller;

import com.codeit.findex.common.dto.PageResponse;
import com.codeit.findex.indexdata.dto.*;
import com.codeit.findex.indexdata.service.IndexDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// --- swagger ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
@Tag(name = "index-data-controller")
public class IndexDataController {

  private final IndexDataService service;

  // 문서화 전용 목록 응답 스키마
  @Schema(name = "IndexDataListResponse", description = "지수 데이터 목록 응답")
  static class IndexDataListDoc {
    @ArraySchema(schema = @Schema(implementation = IndexDataDto.class))
    public List<IndexDataDto> content;
    @Schema(description = "커서(다음 페이지 시작점)", example = "eyJpZCI6MjB9") public String nextCursor;
    @Schema(description = "다음 페이지 조회를 위한 마지막 요소 ID", example = "1") public String nextIdAfter;
    @Schema(description = "페이지 크기", example = "10") public Integer size;
    @Schema(description = "전체 건수", example = "100") public Long totalElements;
    @Schema(description = "다음 페이지 존재 여부", example = "true") public Boolean hasNext;
  }

  // ===== 목록 =====
  @GetMapping
  @Operation(
      summary = "지수 데이터 목록 조회",
      description = "지수 데이터 목록을 조회합니다. 필터링(indexInfoId, 기간), 정렬(sortField, sortDirection), 커서 기반 페이지네이션(cursor/idAfter)을 지원합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = IndexDataListDoc.class),
              examples = @ExampleObject(name = "list-example", value = """
              {
                "content": [
                  {
                    "id": 1,
                    "indexInfoId": 1,
                    "indexName": "IT 서비스",
                    "baseDate": "2023-01-01",
                    "sourceType": "OPEN_API",
                    "marketPrice": 2800.25,
                    "closingPrice": 2850.75,
                    "highPrice": 2870.5,
                    "lowPrice": 2795.3,
                    "versus": 50.5,
                    "fluctuationRate": 1.8,
                    "tradingQuantity": 1250000,
                    "tradingPrice": 3500000000,
                    "marketTotalAmount": 450000000000
                  }
                ],
                "nextCursor": "eyJpZCI6MjB9",
                "nextIdAfter": "1",
                "size": 10,
                "totalElements": 100,
                "hasNext": true
              }
              """))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  public Map<String, Object> list(
      @Parameter(description = "지수 정보 ID")
      @RequestParam(required = false) Long indexInfoId,

      @Parameter(description = "시작 일자", example = "2024-01-01")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

      @Parameter(description = "종료 일자", example = "2024-12-31")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

      @Parameter(description = "이전 페이지 마지막 요소 ID", example = "123")
      @RequestParam(required = false) Long idAfter,

      @Parameter(description = "커서 (다음 페이지 시작점)", example = "eyJpZCI6MjB9")
      @RequestParam(required = false) String cursor,

      // ort 기본값 텍스트가 문서에 보이도록 defaultValue 명시
      @Parameter(description = "정렬 필드",
          schema = @Schema(
              allowableValues = {
                  "baseDate","marketPrice","closingPrice","highPrice","lowPrice",
                  "versus","fluctuationRate","tradingQuantity","tradingPrice","marketTotalAmount"
              },
              defaultValue = "baseDate"
          )
      )
      @RequestParam(defaultValue = "baseDate") String sortField,

      @Parameter(description = "정렬 방향",
          schema = @Schema(allowableValues = {"asc","desc"}, defaultValue = "desc"))
      @RequestParam(defaultValue = "desc") String sortDirection,

      @Parameter(description = "페이지 크기", example = "10")
      @RequestParam(defaultValue = "10") Integer size
  ) {
    PageResponse<IndexDataDto> page = service.list(
        indexInfoId, startDate, endDate, cursor, idAfter, SortField.from(sortField), sortDirection, size
    );

    String nextIdAfterStr = null;
    List<IndexDataDto> content = page.content();
    if (Boolean.TRUE.equals(page.hasNext()) && content != null && !content.isEmpty()) {
      nextIdAfterStr = String.valueOf(content.get(content.size() - 1).id());
    }

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("content", content);
    body.put("nextCursor", page.nextCursor());
    body.put("nextIdAfter", nextIdAfterStr);
    body.put("size", page.size());
    body.put("totalElements", page.totalElements());
    body.put("hasNext", page.hasNext());
    return body;
  }

  // ===== 생성 =====
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "지수 데이터 등록")
  @ApiResponses({
      // POST 응답 예시 고정
      @ApiResponse(responseCode = "201", description = "Created",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = IndexDataDto.class),
              examples = @ExampleObject(name = "create-example", value = """
              {
                "id": 321,
                "indexInfoId": 1,
                "indexName": "IT 서비스",
                "baseDate": "2024-07-31",
                "sourceType": "OPEN_API",
                "marketPrice": 2800.25,
                "closingPrice": 2850.75,
                "highPrice": 2870.5,
                "lowPrice": 2795.3,
                "versus": 50.5,
                "fluctuationRate": 1.8,
                "tradingQuantity": 1250000,
                "tradingPrice": 3500000000,
                "marketTotalAmount": 450000000000
              }
              """))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "404", description = "참조하는 지수 정보를 찾을 수 없음", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  public IndexDataDto create(@Valid @RequestBody IndexDataCreateRequest req) {
    return service.create(req);
  }

  // ===== 수정 =====
  @PatchMapping("/{id}")
  @Operation(summary = "지수 데이터 수정")
  @ApiResponses({
      // PATCH 응답 예시 고정
      @ApiResponse(responseCode = "200", description = "OK",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = IndexDataDto.class),
              examples = @ExampleObject(name = "update-example", value = """
              {
                "id": 321,
                "indexInfoId": 1,
                "indexName": "IT 서비스",
                "baseDate": "2024-07-31",
                "sourceType": "OPEN_API",
                "marketPrice": 2801.00,
                "closingPrice": 2851.00,
                "highPrice": 2871.00,
                "lowPrice": 2796.00,
                "versus": 51.0,
                "fluctuationRate": 1.85,
                "tradingQuantity": 1251000,
                "tradingPrice": 3501000000,
                "marketTotalAmount": 450100000000
              }
              """))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "404", description = "수정할 지수 데이터를 찾을 수 없음", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  public IndexDataDto update(@PathVariable Long id, @RequestBody IndexDataUpdateRequest req) {
    return service.update(id, req);
  }

  // ===== 삭제 =====
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "지수 데이터 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "No Content"),
      @ApiResponse(responseCode = "404", description = "삭제할 지수 데이터를 찾을 수 없음", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

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
