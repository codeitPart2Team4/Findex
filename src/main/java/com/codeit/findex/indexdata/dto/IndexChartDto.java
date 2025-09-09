package com.codeit.findex.indexdata.dto;

import java.util.List;

public record IndexChartDto(
    Long indexInfoId,
    String indexClassification,
    String indexName,
    String periodType,                  // DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY
    List<ChartPoint> dataPoints,       // {date, value}
    List<ChartPoint> ma5DataPoints,    // {date, value}
    List<ChartPoint> ma20DataPoints    // {date, value}
) { }
