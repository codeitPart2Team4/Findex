package com.codeit.findex.indexinfo.service;

import com.codeit.findex.common.dto.PageResponse;
import com.codeit.findex.common.enums.SourceType;
import com.codeit.findex.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.indexinfo.dto.IndexInfoDto;
import com.codeit.findex.indexinfo.dto.IndexInfoSummaryDto;
import com.codeit.findex.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.indexinfo.enums.SortDirection;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.indexinfo.mapper.IndexInfoMapper;
import com.codeit.findex.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;

    private final IndexInfoMapper indexInfoMapper;

    public IndexInfoDto create(IndexInfoCreateRequest request) {

        if (indexInfoRepository.existsByIndexClassificationAndIndexName(
                request.indexClassification(),
                request.indexName())
        ) {
            // 후에 개별 에러로 바꿔야함 - Duplicated
            throw new IllegalStateException();
        }

        IndexInfo indexInfo = new IndexInfo(
                request.indexClassification(),
                request.indexName(),
                request.employedItemsCount(),
                request.basePointInTime(),
                request.baseIndex(),
                SourceType.USER,
                request.favorite()
        );

        return indexInfoMapper.toDto(indexInfoRepository.save(indexInfo));
    }

    public IndexInfoDto findById(Long indexInfoId) {
        return indexInfoMapper.toDto(indexInfoRepository
                .findById(indexInfoId)
                .orElseThrow(NoSuchElementException::new));
    }

    public PageResponse<IndexInfoDto> getIndexInfos(
            String indexClassification,
            String indexName,
            Boolean favorite,
            String cursor,
            String sortField,
            SortDirection sortDirection,
            int size
    ) {
        List<IndexInfo> entities = indexInfoRepository.findAllByConditions(
                indexClassification,
                indexName,
                favorite,
                cursor,
                sortField,
                sortDirection,
                size
        );

        boolean hasNext = entities.size() > size;

        if (hasNext) {
            entities = entities.subList(0, size);
        }

        List<IndexInfoDto> dtos = entities.stream()
                .map(indexInfoMapper::toDto)
                .toList();

        String nextCursor = null;
        if (hasNext && !entities.isEmpty()) {
            IndexInfo last = entities.get(entities.size() - 1);
            nextCursor = buildCursor(last, sortField);
        }

        long totalElements = indexInfoRepository.countByConditions(
                indexClassification,
                indexName,
                favorite
        );

        return new PageResponse<>(
                dtos,
                nextCursor,
                size,
                totalElements,
                hasNext
        );
    }

    private String buildCursor(IndexInfo entity, String sortField) {
        if ("indexClassification".equals(sortField)) {
            return entity.getIndexClassification() + ":" + entity.getId();

        } else if ("indexName".equals(sortField)) {
            return entity.getIndexName() + ":" + entity.getId();

        } else if ("employedItemsCount".equals(sortField)) {
            return entity.getEmployedItemsCount() + ":" + entity.getId();
        }

        return String.valueOf(entity.getId());
    }

    public List<IndexInfoSummaryDto> findSummaryList() {
        return indexInfoRepository.findAll()
                .stream()
                .map(indexInfoMapper::toSummaryDto)
                .toList();
    }

    public IndexInfoDto update(Long indexInfoId, IndexInfoUpdateRequest request) {
        IndexInfo indexInfo = indexInfoRepository.findById(indexInfoId)
                .orElseThrow(NoSuchElementException::new); // 후에 개별 에러로 바꿔야함 - NotFound

        if (request.employedItemsCount() != null) {
            indexInfo.changeEmployedItemsCount(request.employedItemsCount());
        }

        if (request.basePointInTime() != null) {
            indexInfo.changeBasePointInTime(request.basePointInTime());
        }

        if (request.baseIndex() != null) {
            indexInfo.changeBaseIndex(request.baseIndex());
        }

        if (request.favorite() != null) {
            indexInfo.changeFavorite(request.favorite());
        }

        return indexInfoMapper.toDto(indexInfo);
    }

    public void delete(Long id) {
        indexInfoRepository.findById(id)
                .orElseThrow(NoSuchElementException::new); // 후에 개별 에러로 바꿔야함 - NotFound

        indexInfoRepository.deleteById(id);
    }
}