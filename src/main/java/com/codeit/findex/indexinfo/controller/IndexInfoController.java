package com.codeit.findex.indexinfo.controller;

import com.codeit.findex.common.dto.PageResponse;
import com.codeit.findex.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.indexinfo.dto.IndexInfoDto;
import com.codeit.findex.indexinfo.dto.IndexInfoSummaryDto;
import com.codeit.findex.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.indexinfo.enums.SortDirection;
import com.codeit.findex.indexinfo.service.IndexInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/index-infos")
@RequiredArgsConstructor
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

    @PostMapping
    public ResponseEntity<IndexInfoDto> createIndexInfo(
            @RequestBody IndexInfoCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(indexInfoService.create(request));
    }

    @GetMapping("{id}")
    public ResponseEntity<IndexInfoDto> findIndexInfo(
            @PathVariable Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(indexInfoService.findById(id));
    }

    @GetMapping
    public PageResponse<IndexInfoDto> getIndexInfos(
            @RequestParam(required = false) String indexClassification,
            @RequestParam(required = false) String indexName,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "asc") SortDirection sortDirection,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return indexInfoService.getIndexInfos(
                indexClassification,
                indexName,
                favorite,
                cursor,
                sortField,
                sortDirection,
                size
        );
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<IndexInfoSummaryDto>> findIndexInfoSummaryList() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(indexInfoService.findSummaryList());
    }

    @PatchMapping("{id}")
    public ResponseEntity<IndexInfoDto> updateIndexInfo(
            @PathVariable Long id,
            @RequestBody IndexInfoUpdateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(indexInfoService.update(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteIndexInfo(
            @PathVariable Long id
    ) {
        indexInfoService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
