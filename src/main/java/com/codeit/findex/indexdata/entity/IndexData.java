package com.codeit.findex.indexdata.entity;

import com.codeit.findex.common.enums.SourceType;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(
    name = "index_data",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_index_info_base_date",
        columnNames = {"index_info_id", "base_date"}
    )
)
public class IndexData {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "index_info_id", nullable = false)
  private IndexInfo indexInfo;

  private LocalDate baseDate;
  @Enumerated(EnumType.STRING)
  private SourceType sourceType;

  private Double marketPrice;
  private Double closingPrice;
  private Double highPrice;
  private Double lowPrice;
  private Double versus;
  private Double fluctuationRate;
  private Long tradingQuantity;
  private Long tradingPrice;
  private Long marketTotalAmount;
}
