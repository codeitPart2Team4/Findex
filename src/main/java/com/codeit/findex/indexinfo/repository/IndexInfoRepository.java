package com.codeit.findex.indexinfo.repository;

import com.codeit.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>,
        JpaSpecificationExecutor<IndexInfo> {
}
