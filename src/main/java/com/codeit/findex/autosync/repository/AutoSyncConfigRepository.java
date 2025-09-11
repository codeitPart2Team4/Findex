package com.codeit.findex.autosync.repository;

import com.codeit.findex.autosync.entity.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long>, AutoSyncConfigQueryRepository {

    Optional<AutoSyncConfig> findByIndexInfoId(Long indexInfoId);

    List<AutoSyncConfig> findByEnabledTrue();
}
