package com.codeit.findex.autosync.controller;

import com.codeit.findex.autosync.dto.AutoSyncConfigDto;
import com.codeit.findex.autosync.dto.AutoSyncConfigUpdateRequest;
import com.codeit.findex.autosync.dto.CursorPageResponse;
import com.codeit.findex.autosync.service.AutoSyncConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auto-sync-configs")
public class AutoSyncConfigController {

    private final AutoSyncConfigService autoSyncConfigService;

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody AutoSyncConfigUpdateRequest request) {
        if ("null".equalsIgnoreCase(id) || "undefined".equalsIgnoreCase(id)) {
            // 400 Bad Request 응답
            return ResponseEntity.badRequest().body("Invalid id: " + id);
        }

        Long parsedId;
        try {
            parsedId = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid id format: " + id);
        }

        AutoSyncConfigDto updatedDto = autoSyncConfigService.updateAutoSync(parsedId, request.enabled());
        return ResponseEntity.ok(updatedDto);
    }


    @GetMapping
    public ResponseEntity<CursorPageResponse<AutoSyncConfigDto>> getList(
            @RequestParam(required = false) Long indexInfoId,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        CursorPageResponse<AutoSyncConfigDto> response = autoSyncConfigService.getAutoSyncConfigs(indexInfoId, enabled, idAfter, size, direction);
        return ResponseEntity.ok(response);
    }
}
