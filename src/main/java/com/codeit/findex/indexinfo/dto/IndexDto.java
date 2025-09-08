package com.codeit.findex.indexinfo.dto;

import com.codeit.findex.indexinfo.entity.IndexInfo;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class IndexDto {
    private Long id;
    private String indexClassification;
    private String indexName;
    private Integer employedItemsCount;
    private LocalDate basePointInTime;
    private Integer baseIndex;
    private String sourceType;
    private Boolean favorite;

    public static IndexDto fromEntity(IndexInfo entity) {
        return new IndexDto(
                entity.getId(),
                entity.getIndexClassification(),
                entity.getIndexName(),
                entity.getEmployedItemsCount(),
                entity.getBasePointInTime(),
                entity.getBaseIndex(),
                entity.getSourceType(),
                entity.getFavorite()
        );
    }
}