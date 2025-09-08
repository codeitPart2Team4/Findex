package com.codeit.findex.common.dto;

import java.util.List;

public record PageResponse<T>(

        List<T> content,
        String nextCursor,
        int size,
        long totalElements,
        Boolean hasNext
) {}
