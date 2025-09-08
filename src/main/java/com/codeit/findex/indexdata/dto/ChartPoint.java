package com.codeit.findex.indexdata.dto;

import java.time.LocalDate;

public record ChartPoint(
    LocalDate date,
    Double value,
    Double ma5,
    Double ma20
) {}