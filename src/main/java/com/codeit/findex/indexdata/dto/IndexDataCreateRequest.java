package com.codeit.findex.indexdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(name = "IndexDataCreateRequest", description = "지수 데이터 생성 요청")
public record IndexDataCreateRequest(
    @Schema(description = "지수 정보 ID", example = "1") Long indexInfoId,
    @Schema(description = "기준 일자", example = "2024-07-31") LocalDate baseDate,
    @Schema(description = "시가", example = "2800.25") Double marketPrice,
    @Schema(description = "종가", example = "2850.75") Double closingPrice,
    @Schema(description = "고가", example = "2870.5") Double highPrice,
    @Schema(description = "저가", example = "2795.3") Double lowPrice,
    @Schema(description = "대비", example = "50.5") Double versus,
    @Schema(description = "등락률", example = "1.8") Double fluctuationRate,
    @Schema(description = "거래량", example = "1250000") Long tradingQuantity,
    @Schema(description = "거래대금", example = "3500000000") Long tradingPrice,
    @Schema(description = "시가총액", example = "450000000000") Long marketTotalAmount
//  @Schema(hidden = true) String sourceType // 요청 바디에서 받지 않음
) {}