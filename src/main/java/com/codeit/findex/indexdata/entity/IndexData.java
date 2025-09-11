package com.codeit.findex.indexdata.entity;

import com.codeit.findex.common.enums.SourceType;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IndexData)) return false;
    IndexData that = (IndexData) o;
    return Objects.equals(indexInfo.getId(), that.indexInfo.getId()) &&
            Objects.equals(baseDate, that.baseDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(indexInfo.getId(), baseDate);
  }

}
