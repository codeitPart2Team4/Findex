package com.codeit.findex.autosync.repository;

import com.codeit.findex.autosync.entity.AutoSyncConfig;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface AutoSyncConfigQueryRepository {

    List<AutoSyncConfig> findByCursor(Long indexInfoId, Boolean enabled, Long idAfter, int size, Sort.Direction sortDirection);

}
