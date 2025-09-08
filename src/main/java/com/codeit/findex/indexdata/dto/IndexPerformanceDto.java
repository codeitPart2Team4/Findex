package com.codeit.findex.indexdata.dto;

public record IndexPerformanceDto(
    Long indexInfoId,
    String indexName,
    Double currentPrice,
    Double d1ChangePct,
    Double w1ChangePct,
    Double m1ChangePct
) {}