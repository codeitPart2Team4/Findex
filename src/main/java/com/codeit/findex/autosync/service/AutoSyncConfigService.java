package com.codeit.findex.autosync.service;

import com.codeit.findex.autosync.dto.AutoSyncConfigDto;
import com.codeit.findex.autosync.dto.CursorPageResponse;
import com.codeit.findex.autosync.entity.AutoSyncConfig;
import com.codeit.findex.autosync.mapper.AutoSyncMapper;
import com.codeit.findex.autosync.repository.AutoSyncConfigRepository;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.indexinfo.repository.IndexInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AutoSyncConfigService {

    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final AutoSyncMapper autoSyncMapper;

    public CursorPageResponse<AutoSyncConfigDto> getAutoSyncConfigs(
            Long indexInfoId, Boolean enabled, Long idAfter, int size, Sort.Direction direction) {

        // DB에서 조회
        List<AutoSyncConfig> configs = autoSyncConfigRepository.findByCursor(
                indexInfoId, enabled, idAfter, size, direction);

        // Entity -> DTO 변환
        List<AutoSyncConfigDto> dtos = configs.stream()
                .map(autoSyncMapper::toDto)
                .toList();

        // 페이징 처리 정보 계산
        boolean hasNext = configs.size() == size;
        Long nextId = hasNext ? configs.get(dtos.size() - 1).getId() : null;

        return new CursorPageResponse<>(
                dtos, null, nextId, size, dtos.size(), hasNext
        );
    }


    // 자동 연동 설정 업데이트
    @Transactional
    public AutoSyncConfigDto updateAutoSync(Long id, Boolean enabled) {
        AutoSyncConfig config = autoSyncConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("자동 연동 설정이 존재하지 않습니다. id=" + id));

        config.setEnabled(enabled);
        AutoSyncConfig saved = autoSyncConfigRepository.save(config);

        return autoSyncMapper.toDto(saved);
    }
}
