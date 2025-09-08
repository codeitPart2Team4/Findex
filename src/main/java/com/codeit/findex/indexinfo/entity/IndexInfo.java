package com.codeit.findex.indexinfo.entity;

import com.codeit.findex.common.entity.BaseEntity;
import com.codeit.findex.common.enums.SourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "index_info")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false)
    private Boolean autoSyncEnabled = false;

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

    public void changeAutoSyncEnabled(Boolean autoSyncEnabled) {
        this.autoSyncEnabled = autoSyncEnabled;
    }
}
