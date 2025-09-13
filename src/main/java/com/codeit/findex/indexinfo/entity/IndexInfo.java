package com.codeit.findex.indexinfo.entity;

import com.codeit.findex.common.entity.BaseEntity;
import com.codeit.findex.common.enums.SourceType;
import com.codeit.findex.indexdata.entity.IndexData;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "index_info")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor/*(access = AccessLevel.PROTECTED)*/
public class IndexInfo extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String indexClassification;

    @Column(nullable = false, unique = true, length = 100)
    private String indexName;

    private Integer employedItemsCount;

    private LocalDate basePointInTime;

    private BigDecimal baseIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SourceType sourceType;

    @Column(nullable = false)
    private Boolean favorite = false;

    @OneToMany(mappedBy = "indexInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndexData> indexDataList = new ArrayList<>();

    public IndexInfo(String indexClassification, String indexName, Integer employedItemsCount, LocalDate basePointInTime, BigDecimal baseIndex, SourceType sourceType, Boolean favorite) {
        this.indexClassification = indexClassification;
        this.indexName = indexName;
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.sourceType = sourceType;
        this.favorite = favorite;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        IndexInfo indexInfo = (IndexInfo) object;
        return Objects.equals(indexClassification, indexInfo.indexClassification) && Objects.equals(indexName, indexInfo.indexName) && Objects.equals(basePointInTime, indexInfo.basePointInTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexClassification, indexName, basePointInTime);
    }

    public void changeEmployedItemsCount(Integer employedItemsCount) {
        this.employedItemsCount = employedItemsCount;
    }

    public void changeBasePointInTime(LocalDate basePointInTime) {
        this.basePointInTime = basePointInTime;
    }

    public void changeBaseIndex(BigDecimal baseIndex) {
        this.baseIndex = baseIndex;
    }

    public void changeFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
