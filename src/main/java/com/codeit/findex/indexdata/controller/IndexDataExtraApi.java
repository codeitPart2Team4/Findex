package com.codeit.findex.indexdata.controller;

import com.codeit.findex.indexdata.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "index-data-extra-controller")
public interface IndexDataExtraApi {

  // ==== 차트 ====
  @Operation(summary = "지수 차트 조회", description = "지수의 차트 데이터를 조회합니다.")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "차트 데이터 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = IndexChartDto.class),
              examples = @ExampleObject(value = """
              {
                "indexInfoId": 1,
                "indexClassification": "KOSPI시리즈",
                "indexName": "IT 서비스",
                "periodType": "DAILY",
                "dataPoints": [{"date":"2023-01-01","value":2850.75}],
                "ma5DataPoints": [{"date":"2023-01-01","value":2850.75}],
                "ma20DataPoints": [{"date":"2023-01-01","value":2850.75}]
              }
              """)
          )
      ),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 기간 유형 등)"),
      @ApiResponse(responseCode = "404", description = "지수 정보를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  IndexChartDto chart(
      @Parameter(description = "지수 정보 ID") Long indexInfoId,
      @Parameter(
          description = "차트 기간 유형",
          schema = @Schema(
              allowableValues = {"DAILY","WEEKLY","MONTHLY","QUARTERLY","YEARLY"},
              defaultValue = "DAILY"
          )
      )
      String periodType
  );

  // ==== 성과 랭킹 ====
  @Operation(summary = "지수 성과 랭킹 조회", description = "지수의 성과 분석 랭킹을 조회합니다.")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "성과 랭킹 조회 성공",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = IndexPerformanceWithRankDto.class)),
              examples = @ExampleObject(value = """
              [
                {
                  "performance": {
                    "indexInfoId": 1,
                    "indexClassification": "KOSPI시리즈",
                    "indexName": "IT 서비스",
                    "versus": 50.5,
                    "fluctuationRate": 1.8,
                    "currentPrice": 2850.75,
                    "beforePrice": 2850.75
                  },
                  "rank": 1
                }
              ]
              """)
          )
      ),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 기간 유형 등)"),
      @ApiResponse(responseCode = "404", description = "지수 정보를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  List<IndexPerformanceWithRankDto> rank(
      @Parameter(description = "지수 정보 ID") Long indexInfoId,
      @Parameter(
          description = "성과 기간 유형",
          schema = @Schema(allowableValues = {"DAILY","WEEKLY","MONTHLY"}, defaultValue = "DAILY")
      )
      String periodType,
      @Parameter(description = "최대 랭킹 수", example = "10") Integer limit
  );

  // ==== 관심 성과 ====
  @Operation(summary = "관심 지수 성과 조회", description = "즐겨찾기로 등록된 지수들의 성과를 조회합니다.")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "관심 지수 성과 조회 성공",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = IndexPerformanceDto.class)),
              examples = @ExampleObject(value = """
              [
                {
                  "indexInfoId": 1,
                  "indexClassification": "KOSPI시리즈",
                  "indexName": "IT 서비스",
                  "versus": 50.5,
                  "fluctuationRate": 1.8,
                  "currentPrice": 2850.75,
                  "beforePrice": 2850.75
                }
              ]
              """)
          )
      ),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  List<IndexPerformanceDto> favorite(
      @Parameter(
          description = "성과 기간 유형",
          schema = @Schema(allowableValues = {"DAILY","WEEKLY","MONTHLY"}, defaultValue = "DAILY")
      )
      String periodType
  );

  // ==== CSV Export ====
  @Operation(summary = "지수 데이터 CSV export", description = "지수 데이터를 CSV 파일로 export합니다.")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "CSV 파일 생성 성공",
          content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))
      ),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  ResponseEntity<byte[]> exportCsv(
      @Parameter(description = "지수 정보 ID") Long indexInfoId,
      @Parameter(description = "시작 일자", example = "2024-01-01") LocalDate startDate,
      @Parameter(description = "종료 일자", example = "2024-12-31") LocalDate endDate,
      @Parameter(
          description = "정렬 필드",
          schema = @Schema(
              allowableValues = {
                  "baseDate","marketPrice","closingPrice","highPrice","lowPrice",
                  "versus","fluctuationRate","tradingQuantity","tradingPrice","marketTotalAmount"
              },
              defaultValue = "baseDate"
          )
      )
      String sortField,
      @Parameter(
          description = "정렬 방향",
          schema = @Schema(allowableValues = {"asc","desc"}, defaultValue = "desc")
      )
      String sortDirection
  );
}
