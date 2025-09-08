package com.codeit.findex.indexdata.dto;

public record IndexPerformanceWithRankDto(
    IndexPerformanceDto performance,
    int rank
) {}