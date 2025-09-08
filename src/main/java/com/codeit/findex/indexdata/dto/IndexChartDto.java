package com.codeit.findex.indexdata.dto;

import java.util.List;

public record IndexChartDto(
    Long indexInfoId,
    String indexName,
    List<ChartPoint> dataPoints
) {}