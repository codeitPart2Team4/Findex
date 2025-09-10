package com.codeit.findex.autosync.dto;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
) {}
