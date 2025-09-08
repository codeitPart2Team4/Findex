package com.codeit.findex.indexdata.dto;

public record IndexDataUpdateRequest(
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