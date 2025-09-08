package com.codeit.findex.indexdata.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record IndexDataCreateRequest(
    @NotNull Long indexInfoId,
    @NotNull LocalDate baseDate,
    String sourceType,          // 미지정 시 서비스에서 OPEN_API
    Double marketPrice,
    Double closingPrice,
    Double highPrice,
    Double lowPrice,
    Double versus,
    Double fluctuationRate,
    Long tradingQuantity,
    Long tradingPrice,
    Long marketTotalAmount
) {}