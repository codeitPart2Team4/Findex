package com.codeit.findex.indexinfo.repository;

import com.codeit.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>, IndexInfoQueryRepository {

    Boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);
}
