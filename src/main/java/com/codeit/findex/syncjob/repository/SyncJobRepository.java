package com.codeit.findex.syncjob.repository;

import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long> {

    Optional<SyncJob> findByIndexInfoAndJobType(IndexInfo indexInfo, String jobType);
}
