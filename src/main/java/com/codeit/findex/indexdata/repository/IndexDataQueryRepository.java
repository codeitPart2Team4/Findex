package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.controller.SortField;
import com.codeit.findex.indexdata.entity.IndexData;

import java.time.LocalDate;
import java.util.List;

public interface IndexDataQueryRepository {

  List<IndexData> findForList(Long indexInfoId,
      LocalDate startDate,
      LocalDate endDate,
      SortField sortField,
      boolean desc,
      Double pivotSortVal,
      Long pivotId,
      int limitPlusOne);

  long countForList(Long indexInfoId, LocalDate startDate, LocalDate endDate);
}