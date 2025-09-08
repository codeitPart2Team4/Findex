package com.codeit.findex.indexdata.repository;

import com.codeit.findex.common.paging.CursorPageResponse;
import com.codeit.findex.indexdata.dto.IndexDataDto;

import java.time.LocalDate;

public interface IndexDataRepositoryCustom {
  enum SortKey { DATE, CLOSE, VOLUME, TURNOVER /* MARKET_CAP 등 추후 확장 */ }

  CursorPageResponse<IndexDataDto> search(
      Long indexInfoId, LocalDate startDate, LocalDate endDate,
      SortKey sort, boolean asc, Long prevLastId, int size
  );
}