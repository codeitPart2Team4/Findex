package com.codeit.findex.syncjob.repository;

import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long>, SyncJobQueryRepository {

    Optional<SyncJob> findByIndexInfoAndJobType(IndexInfo indexInfo, String jobType);

    Optional<SyncJob> findByIndexInfoAndJobTypeAndTargetDate(IndexInfo indexInfo, String jobType, LocalDate targetDate);

    @Modifying
    @Query("delete from SyncJob sj where sj.indexInfo.id = :indexInfoId")
    void deleteByIndexInfoId(@Param("indexInfoId") Long indexInfoId);
}
