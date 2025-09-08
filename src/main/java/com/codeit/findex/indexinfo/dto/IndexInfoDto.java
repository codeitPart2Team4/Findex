package com.codeit.findex.indexinfo.dto;

import com.codeit.findex.common.enums.SourceType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoDto(
        Long id,
        String indexClassification,
        String indexName,
        Integer employedItemsCount,
        LocalDate basePointInTime,
        BigDecimal baseIndex,
        SourceType sourceType,
        Boolean favorite,
        Boolean autoSyncEnabled
) {}
