package com.codeit.findex.indexdata.controller;

import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "index-data-controller")
public interface IndexDataApi {

  // ==== 문서 전용 스키마 ====
  @Schema(name = "IndexDataListResponse", description = "지수 데이터 목록 응답")
  class IndexDataListDoc {

    @ArraySchema(schema = @Schema(implementation = IndexDataDto.class))
    public List<IndexDataDto> content;
    @Schema(description = "커서(다음 페이지 시작점)", example = "eyJpZCI6MjB9")
    public String nextCursor;
    @Schema(description = "다음 페이지 조회를 위한 마지막 요소 ID", example = "eyJpZCI6MjB9")
    public String nextIdAfter;
    @Schema(description = "페이지 크기", example = "10")
    public Integer size;
    @Schema(description = "전체 건수", example = "100")
    public Long totalElements;
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    public Boolean hasNext;
  }

  @Schema(name = "ErrorResponse")
  class ErrorResponse {

    @Schema(example = "2025-09-10T05:39:06.152068Z")
    public String timestamp;
    @Schema(example = "400")
    public Integer status;
    @Schema(example = "잘못된 요청입니다.")
    public String message;
    @Schema(example = "부서 코드는 필수입니다.")
    public String details;
  }

  // ==== 목록 ====
  @Operation(
      summary = "지수 데이터 목록 조회",
      description = "지수 데이터 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200", description = "지수 데이터 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = IndexDataListDoc.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "content": [
                          {
                            "id": 1,
                            "indexInfoId": 1,
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
                        "nextIdAfter": "eyJpZCI6MjB9",
                        "size": 10,
                        "totalElements": 100,
                        "hasNext": true
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-09-10T05:39:06.152068Z",
                        "status": 400,
                        "message": "잘못된 요청입니다.",
                        "details": "부서 코드는 필수입니다."
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2025-09-10T05:39:06.152068Z",
                        "status": 500,
                        "message": "서버 오류가 발생했습니다.",
                        "details": "예상치 못한 오류입니다."
                      }
                      """
              )
          )
      )
  })
  Map<String, Object> list(
      @Parameter(description = "지수 정보 ID", required = false, example = "1")
      Long indexInfoId,
      @Parameter(
          description = "시작 일자",
          required = false,
          schema = @Schema(type = "string", format = "date", example = "2024-01-01"))
      LocalDate startDate,
      @Parameter(
          description = "종료 일자",
          required = false,
          schema = @Schema(type = "string", format = "date", example = "2024-12-31"))
      LocalDate endDate,
      @Parameter(description = "이전 페이지 마지막 요소 ID", required = false, example = "123")
      Long idAfter,
      @Parameter(description = "커서 (다음 페이지 시작점)", required = false, example = "eyJpZCI6MjB9")
      String cursor,
      @Parameter(
          description = "정렬 필드",
          schema = @Schema(
              allowableValues = {
                  "baseDate", "marketPrice", "closingPrice", "highPrice", "lowPrice",
                  "versus", "fluctuationRate", "tradingQuantity", "tradingPrice",
                  "marketTotalAmount"
              },
              defaultValue = "baseDate"))
      String sortField,
      @Parameter(
          description = "정렬 방향",
          schema = @Schema(allowableValues = {"asc", "desc"}, defaultValue = "desc"))
      String sortDirection,
      @Parameter(
          description = "페이지 크기",
          schema = @Schema(type = "integer", format = "int32", defaultValue = "10", example = "10"))
      Integer size
  );

   // ==== 생성 ====
  @Operation(summary = "지수 데이터 등록", description = "새로운 지수 데이터를 등록합니다.")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201", description = "지수 데이터 생성 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = IndexDataDto.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "id": 1,
                        "indexInfoId": 1,
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
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404", description = "참조하는 지수 정보를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  IndexDataDto create(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = IndexDataCreateRequest.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "indexInfoId": 1,
                        "baseDate": "2023-01-01",
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
                      """
              )
          )
      )
      IndexDataCreateRequest req
  );

  // ==== 수정 ====
  @Operation(summary = "지수 데이터 수정", description = "기존 지수 데이터를 수정합니다.")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200", description = "지수 데이터 수정 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = IndexDataDto.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "id": 1,
                        "indexInfoId": 1,
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
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      { "timestamp": "2025-09-10T05:39:06.152068Z",
                        "status": 400, "message": "잘못된 요청입니다.",
                        "details": "필드 유효성 오류" }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404", description = "수정할 지수 데이터를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  IndexDataDto update(
      @Parameter(name = "id", description = "지수 데이터 ID", example = "1", required = true, in = ParameterIn.PATH)
      Long id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = IndexDataUpdateRequest.class),
              examples = @ExampleObject(
                  value = """
                      {
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
                      """
              )
          )
      )
      IndexDataUpdateRequest req
  );

// ==== 삭제 ====
  @Operation(summary = "지수 데이터 삭제", description = "지수 데이터를 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "지수 데이터 삭제 성공"),
      @ApiResponse(
          responseCode = "404", description = "삭제할 지수 데이터를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      { "timestamp": "2025-09-10T05:39:06.152068Z",
                        "status": 404, "message": "데이터를 찾을 수 없습니다.",
                        "details": "id=1" }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  void delete(
      @Parameter(name = "id", description = "지수 데이터 ID", example = "1", required = true, in = ParameterIn.PATH)
      Long id
  );
}

