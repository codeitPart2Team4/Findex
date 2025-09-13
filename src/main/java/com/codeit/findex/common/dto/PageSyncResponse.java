package com.codeit.findex.common.dto;

import java.util.List;

public record PageSyncResponse<T>(
    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    Integer size,
    Long totalElements,
    Boolean hasNext
) { }
