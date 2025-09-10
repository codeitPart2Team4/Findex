package com.codeit.findex.autosync.dto;

public record AutoSyncConfigDto(
        Long id,
        Long indexInfoId,
        String indexClassification,
        String indexName,
        Boolean enabled
) {}