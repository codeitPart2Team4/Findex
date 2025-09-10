package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.dto.ChartPointDto;
import com.codeit.findex.indexdata.dto.PerformanceRankDto;

import java.time.LocalDate;
import java.util.List;

public interface IndexDataExtraRepository {

  List<ChartPointDto> findChartPoints(Long indexInfoId, LocalDate start, LocalDate end);

  // favoriteOnly=true 면 즐겨찾기 지수만
  List<PerformanceRankDto> findPerformanceRank(LocalDate start, LocalDate end, boolean favoriteOnly, int size);

  // CSV export용 raw row
  List<Object[]> findForCsv(Long indexInfoId, LocalDate start, LocalDate end);
}
