package com.codeit.findex.indexinfo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoCreateRequest (

    String indexClassification,
    String indexName,
    Integer employedItemsCount,
    LocalDate basePointInTime,
    BigDecimal baseIndex,
    Boolean favorite,
    Boolean autoSyncEnabled
) {}
