package com.codeit.findex.indexinfo.repository;

import com.codeit.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>, IndexInfoQueryRepository {

    Boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);

    Optional<IndexInfo> findByIndexName(String idxNm);

    IndexInfo findByIndexNameAndIndexClassification(String indexName, String indexName1);
}
