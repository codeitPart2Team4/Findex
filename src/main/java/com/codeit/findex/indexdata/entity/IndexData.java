package com.codeit.findex.indexdata.entity;

import com.codeit.findex.common.model.SourceType;
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

  private LocalDate baseDate;          // → base_date
  @Enumerated(EnumType.STRING)
  private SourceType sourceType;       // → source_type

  private Double marketPrice;          // → market_price
  private Double closingPrice;         // → closing_price
  private Double highPrice;            // → high_price
  private Double lowPrice;             // → low_price
  private Double versus;               // → versus
  private Double fluctuationRate;      // → fluctuation_rate
  private Long tradingQuantity;        // → trading_quantity
  private Long tradingPrice;           // → trading_price
  private Long marketTotalAmount;      // → market_total_amount
}
