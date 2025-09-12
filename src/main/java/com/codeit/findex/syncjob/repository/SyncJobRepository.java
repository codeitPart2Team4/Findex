package com.codeit.findex.syncjob.repository;

import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long>, SyncJobQueryRepository {

    Optional<SyncJob> findByIndexInfoAndJobType(IndexInfo indexInfo, String jobType);

    Optional<SyncJob> findByIndexInfoAndJobTypeAndTargetDate(IndexInfo indexInfo, String jobType, LocalDate targetDate);
}
