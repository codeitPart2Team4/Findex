package com.codeit.findex.common.paging;

import java.util.List;
public record CursorPageResponse<T>(List<T> content, int size, boolean hasNext, Long lastId) {}