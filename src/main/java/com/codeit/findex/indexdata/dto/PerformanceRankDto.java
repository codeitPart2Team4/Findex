package com.codeit.findex.indexdata.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PerformanceRankDto {
  private Long   indexInfoId;
  private String indexName;
  private String indexClassification;

  private Double firstClose;
  private Double lastClose;
  private Double absChange;  // last - first
  private Double returnPct;  // ((last-first)/first)*100
}