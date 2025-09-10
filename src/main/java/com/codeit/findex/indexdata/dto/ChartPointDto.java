package com.codeit.findex.indexdata.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChartPointDto {
  private LocalDate date;   // baseDate
  private Double open;      // marketPrice
  private Double close;     // closingPrice
  private Double high;
  private Double low;
  private Long   volume;    // tradingQuantity
}
