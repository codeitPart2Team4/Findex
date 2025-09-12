package com.codeit.findex.syncjob.service;

import com.codeit.findex.common.dto.PageResponse;
import com.codeit.findex.common.error.errorcode.SyncJobErrorCode;
import com.codeit.findex.common.error.exception.SyncJobException;
import com.codeit.findex.data.DataSyncRepository;
import com.codeit.findex.data.IndexApiParser;
import com.codeit.findex.data.dto.Body;
import com.codeit.findex.data.dto.Item;
import com.codeit.findex.common.enums.SortDirection;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.syncjob.dto.IndexDataSyncRequest;
import com.codeit.findex.syncjob.dto.SyncJobDto;
import com.codeit.findex.syncjob.entity.SyncJob;
import com.codeit.findex.syncjob.mapper.SyncJobMapper;
import com.codeit.findex.syncjob.repository.SyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SyncJobService {

    private final SyncJobRepository syncJobRepository;

    private final IndexInfoRepository indexInfoRepository;

    private final DataSyncRepository dataSyncRepository;

    private final IndexApiParser indexApiParser;

    private final SyncJobMapper syncJobMapper;

    public List<SyncJobDto> syncIndexInfo(String ip) {
        int pageSize = 1000;
        int numOfRows = 1000;

        List<SyncJobDto> syncJobDtoList = new ArrayList<>();

        List<IndexInfo> indexInfoList = indexInfoRepository.findAll();

        for (IndexInfo indexInfo : indexInfoList) {
            syncJobDtoList.addAll(fetchAndStoreIndexInfo(indexInfo.getIndexName(), numOfRows, pageSize, ip));
        }

        return syncJobDtoList;
    }

    public List<SyncJobDto> syncIndexData(IndexDataSyncRequest request, String ip) {
        int pageSize = 1000;
        int numOfRows = 1000;

        List<SyncJobDto> syncJobDtoList = new ArrayList<>();

        if (request.indexInfoIds().isEmpty()) {
            syncJobDtoList.addAll(fetchAndStoreIndexData(null, request.baseDateFrom(), request.baseDateTo(), numOfRows, pageSize, ip));
        } else {
            for(Long indexId : request.indexInfoIds()) {
                if (!indexInfoRepository.existsById(indexId)) {
                    throw new SyncJobException(SyncJobErrorCode.ASYNC_JOB_INVALID_REQUEST);
                }
                String indexName = indexInfoRepository.findById(indexId).get().getIndexName();
                syncJobDtoList.addAll(fetchAndStoreIndexData(indexName, request.baseDateFrom(), request.baseDateTo(), numOfRows, pageSize, ip));
            }
        }

        return syncJobDtoList;
    }

    public PageResponse<SyncJobDto> getSyncJobs(
            String          jobType,
            Long            indexInfoId,
            LocalDate       baseDateFrom,
            LocalDate       baseDateTo,
            String          worker,
            LocalDateTime   jobTimeFrom,
            LocalDateTime   jobTimeTo,
            String          status,
            Long            idAfter,
            String          cursor,
            String          sortField,
            SortDirection   sortDirection,
            int             size
    ) {
        List<SyncJob> syncJobs = syncJobRepository.findAllByConditions(
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

        boolean hasNext= syncJobs.size() > size;

        if (hasNext) {
            syncJobs = syncJobs.subList(0, size);
        }

        List<SyncJobDto> syncJobDtos = syncJobs.stream()
                .map(syncJobMapper::toDto)
                .toList();

        String nextCursor = null;
        if(hasNext && !syncJobs.isEmpty()) {
            SyncJob last = syncJobs.get(syncJobs.size() - 1);
            nextCursor = buildCursor(last, sortField);
        }

        long totalElements = syncJobRepository.countByConditions(
                jobType,
                indexInfoId,
                baseDateFrom,
                baseDateTo,
                worker,
                status,
                jobTimeFrom,
                jobTimeTo
        );

        return new PageResponse<>(
                syncJobDtos,
                nextCursor,
                size,
                totalElements,
                hasNext
        );
    }

    private String buildCursor(SyncJob entity, String sortField) {
        if ("jobType".equals(sortField)) {
           return entity.getJobType() + ":" + entity.getId();
        } else if ("targetDate".equals(sortField)) {
            return entity.getTargetDate() + ":" + entity.getId();
        }

        return String.valueOf(entity.getId());
    }

    private List<SyncJobDto> fetchAndStoreIndexInfo(String indexName, int numOfRows, int pageSize, String ip) {
        int pageNo = 1;
        int totalPages = Integer.MAX_VALUE;
        List<SyncJobDto> syncJobDtoList = new ArrayList<>();

        while(pageNo <= totalPages) {
            try {
                StringBuilder urlBuilder = dataSyncRepository.createUrl(pageNo, numOfRows, indexName);
                String responseJson = dataSyncRepository.callApiWithRetry(urlBuilder);

                if (responseJson == null || responseJson.isEmpty()) {
                    System.out.println("responseJson에 문제 발생.");
                    break;
                }

                Body body = indexApiParser.parseBody(responseJson);
                System.out.println("총 데이터 수: " + body.getTotalCount());

                if (pageNo == 1) {
                    totalPages = (int) Math.ceil(body.getTotalCount() / (double) pageSize);
                }

                List<Item> items = indexApiParser.parseItems(responseJson);
                syncJobDtoList.addAll(dataSyncRepository.storeIndexInfoToDb(items, ip).stream()
                        .filter(data -> data.getJobType().equals("INDEX_INFO"))
                        .map(syncJobMapper::toDto).toList());

                pageNo++;
            } catch (InterruptedException e) {
                System.err.println("동기화 실패: ");
                e.printStackTrace();
                break;
            }
        }
        return syncJobDtoList;
    }


    private List<SyncJobDto> fetchAndStoreIndexData(String indexName, LocalDate baseDateFrom, LocalDate baseDateTo, int numOfRows, int pageSize, String ip) {
        int pageNo = 1;
        int totalPages = Integer.MAX_VALUE;
        List<SyncJobDto> syncJobDtoList = new ArrayList<>();

        while (pageNo <= totalPages) {
            try {
                StringBuilder urlBuilder = indexName == null
                    ? dataSyncRepository.createUrl(pageNo, numOfRows, baseDateFrom, baseDateTo)
                    : dataSyncRepository.createUrl(pageNo, numOfRows, indexName, baseDateFrom, baseDateTo);
                String responseJson = dataSyncRepository.callApiWithRetry(urlBuilder);

                if (responseJson == null || responseJson.isEmpty()) {
                    System.out.println("responseJson에 문제 발생.");
                    break;
                }

                Body body = indexApiParser.parseBody(responseJson);
                System.out.println("총 데이터 수: " + body.getTotalCount());

                if (pageNo == 1) {
                    totalPages = (int) Math.ceil(body.getTotalCount() / (double) pageSize);
                }

                List<Item> items = indexApiParser.parseItems(responseJson);

                System.out.println("processItems 호출 - 아이템 수: " + items.size());
                syncJobDtoList.addAll(dataSyncRepository.storeIndexDataToDb(items, ip)
                        .stream().filter(data -> data.getJobType().equals("INDEX_DATA"))
                        .map(syncJobMapper::toDto).toList());

                pageNo++;
            } catch (InterruptedException e) {
                System.err.println("동기화 실패: ");
                e.printStackTrace();
                break;
            }
        }

        return syncJobDtoList;
    }

}
