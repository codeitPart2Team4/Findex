package com.codeit.findex.indexdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "IndexDataUpdateRequest", description = "지수 데이터 수정 요청")
public record IndexDataUpdateRequest(
    @Schema(description = "시가", example = "2800.25") Double marketPrice,
    @Schema(description = "종가", example = "2850.75") Double closingPrice,
    @Schema(description = "고가", example = "2870.5") Double highPrice,
    @Schema(description = "저가", example = "2795.3") Double lowPrice,
    @Schema(description = "대비", example = "50.5") Double versus,
    @Schema(description = "등락률", example = "1.8") Double fluctuationRate,
    @Schema(description = "거래량", example = "1250000") Long tradingQuantity,
    @Schema(description = "거래대금", example = "3500000000") Long tradingPrice,
    @Schema(description = "시가총액", example = "450000000000") Long marketTotalAmount
//  @Schema(hidden = true) String sourceType // 숨김 처리
) {}