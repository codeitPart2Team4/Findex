package com.codeit.findex.indexdata.dto;

import java.time.LocalDate;

public record IndexDataDto(
    Long id,
    Long indexInfoId,
    String indexName,
    LocalDate baseDate,
    String sourceType,
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