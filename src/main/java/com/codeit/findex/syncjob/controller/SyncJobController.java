package com.codeit.findex.syncjob.controller;

import com.codeit.findex.common.dto.PageResponse;
import com.codeit.findex.common.enums.SortDirection;
import com.codeit.findex.syncjob.dto.IndexDataSyncRequest;
import com.codeit.findex.syncjob.dto.SyncJobDto;
import com.codeit.findex.syncjob.service.SyncJobService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sync-jobs")
@RequiredArgsConstructor
public class SyncJobController {

    private final SyncJobService syncJobService;

    @PostMapping("/index-infos")
    public ResponseEntity<List<SyncJobDto>> syncJobIndexInfo(HttpServletRequest httpServletRequest) {
        String clientIp = getClientIp(httpServletRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(syncJobService.syncIndexInfo(clientIp));
    }

    @PostMapping("/index-data")
    public ResponseEntity<List<SyncJobDto>> syncJobIndexData(
            @RequestBody IndexDataSyncRequest request,
            HttpServletRequest httpServletRequest) {
        String clientIp = getClientIp(httpServletRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(syncJobService.syncIndexData(request, clientIp));
    }

    @GetMapping
    public PageResponse<SyncJobDto> getSyncJobs(
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Long indexInfoId,
            @RequestParam(required = false) LocalDate baseDateFrom,
            @RequestParam(required = false) LocalDate baseDateTo,
            @RequestParam(required = false) String worker,
            @RequestParam(required = false) LocalDateTime jobTimeFrom,
            @RequestParam(required = false) LocalDateTime jobTimeTo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "jobTime") String sortField,
            @RequestParam(required = false, defaultValue = "desc") SortDirection sortDirection,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return syncJobService.getSyncJobs(
                jobType,
                indexInfoId,
                baseDateFrom,
                baseDateTo,
                worker,
                jobTimeFrom,
                jobTimeTo,
                status,
                idAfter,
                sortField,
                cursor,
                sortDirection,
                size
        );
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 는 다중 IP 일 수 있어 첫번째만 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
