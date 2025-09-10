package com.codeit.findex.indexdata.controller;

import com.codeit.findex.common.dto.PageResponse;
import com.codeit.findex.indexdata.dto.*;
import com.codeit.findex.indexdata.service.IndexDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
@Tag(name = "index-data-controller")
public class IndexDataController implements IndexDataApi {

  private final IndexDataService service;

  // ===== 목록 =====
  @GetMapping
  @Operation(summary = "지수 데이터 목록 조회")
  public Map<String, Object> list(
      @RequestParam(required = false) Long indexInfoId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "baseDate") String sortField,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(defaultValue = "10") Integer size
  ) {
    PageResponse<IndexDataDto> page = service.list(
        indexInfoId, startDate, endDate, cursor, idAfter, SortField.from(sortField), sortDirection, size
    );

    String nextIdAfterStr = null;
    List<IndexDataDto> content = page.content();
    if (Boolean.TRUE.equals(page.hasNext()) && content != null && !content.isEmpty()) {
      nextIdAfterStr = String.valueOf(content.get(content.size() - 1).id());
    }

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("content", content);
    body.put("nextCursor", page.nextCursor());
    body.put("nextIdAfter", nextIdAfterStr);
    body.put("size", page.size());
    body.put("totalElements", page.totalElements());
    body.put("hasNext", page.hasNext());
    return body;
  }

  // ===== 생성 =====
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "지수 데이터 등록")
  public IndexDataDto create(@Valid @RequestBody IndexDataCreateRequest req) {
    return service.create(req);
  }

  // ===== 수정 =====
  @PatchMapping("/{id}")
  @Operation(summary = "지수 데이터 수정")
  public IndexDataDto update(@PathVariable Long id, @RequestBody IndexDataUpdateRequest req) {
    return service.update(id, req);
  }

  // ===== 삭제 =====
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "지수 데이터 삭제")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
