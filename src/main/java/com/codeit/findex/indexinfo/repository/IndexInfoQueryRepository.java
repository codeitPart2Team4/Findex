package com.codeit.findex.indexinfo.repository;

import com.codeit.findex.indexinfo.enums.SortDirection;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.domain.Page;

public interface IndexInfoQueryRepository {

    Page<IndexInfo> findAllByConditions(
            String indexClassification,
            String indexName,
            Boolean favorite,
            String cursor,
            String sortField,
            SortDirection sortDirection,
            int size
    );
}
