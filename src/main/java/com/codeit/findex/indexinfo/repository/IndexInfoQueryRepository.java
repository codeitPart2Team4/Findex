package com.codeit.findex.indexinfo.repository;

import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.common.enums.SortDirection;

import java.util.List;

public interface IndexInfoQueryRepository {

    List<IndexInfo> findAllByConditions(
            String indexClassification,
            String indexName,
            Boolean favorite,
            String cursor,
            String sortField,
            SortDirection sortDirection,
            int size
    );

    long countByConditions(String indexClassification, String indexName, Boolean favorite);
}
