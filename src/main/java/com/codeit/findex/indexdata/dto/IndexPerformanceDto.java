package com.codeit.findex.indexdata.dto;

public record IndexPerformanceDto(
    Long   indexInfoId,          // 지수 ID
    String indexClassification,  // 지수 분류명
    String indexName,            // 지수명
    Double versus,               // 대비 = current - before
    Double fluctuationRate,      // 등락률(%) = versus / before * 100
    Double currentPrice,         // 현재가(최근 종가)
    Double beforePrice           // 비교 기준가(전일/1주전/1개월전)
) {}