package com.codeit.findex.indexdata.service;

import com.codeit.findex.indexdata.dto.ChartPointDto;
import com.codeit.findex.indexdata.dto.PerformanceRankDto;
import com.codeit.findex.indexdata.repository.IndexDataExtraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexDataExtraService {

  private final IndexDataExtraRepository repo;

  public List<ChartPointDto> getChart(Long indexInfoId, LocalDate start, LocalDate end) {
    return repo.findChartPoints(indexInfoId, start, end);
  }

  public List<PerformanceRankDto> getPerformanceRank(LocalDate start, LocalDate end, int size) {
    return repo.findPerformanceRank(start, end, false, size);
  }

  public List<PerformanceRankDto> getFavoritePerformance(LocalDate start, LocalDate end, int size) {
    return repo.findPerformanceRank(start, end, true, size);
  }

  public void writeCsv(Long indexInfoId, LocalDate start, LocalDate end, PrintWriter out) {
    out.println("date,open,close,high,low,versus,fluctuation_rate,volume,trading_price,market_cap,source_type");
    for (Object[] r : repo.findForCsv(indexInfoId, start, end)) {
      String line = String.join(",",
          String.valueOf(r[0]),
          nul(r[1]), nul(r[2]), nul(r[3]), nul(r[4]),
          nul(r[5]), nul(r[6]),
          nul(r[7]), nul(r[8]), nul(r[9]),
          String.valueOf(r[10])
      );
      out.println(line);
    }
    out.flush();
  }

  private String nul(Object o) { return o==null? "" : o.toString(); }
}
