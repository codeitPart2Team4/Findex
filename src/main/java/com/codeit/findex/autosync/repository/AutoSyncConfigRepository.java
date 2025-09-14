package com.codeit.findex.autosync.repository;

import com.codeit.findex.autosync.entity.AutoSyncConfig;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long>, AutoSyncConfigQueryRepository {

    Optional<AutoSyncConfig> findByIndexInfoId(Long indexInfoId);

    List<AutoSyncConfig> findByEnabledTrue();

    @Modifying(clearAutomatically = true)
    @Query("delete from AutoSyncConfig a where a.indexInfo.id = :indexInfoId")
    void deleteByIndexInfoId(@Param("indexInfoId") Long indexInfoId);
}
