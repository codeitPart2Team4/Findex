package com.codeit.findex.syncjob.service;

import com.codeit.findex.common.dto.PageResponse;
import com.codeit.findex.common.error.errorcode.IndexInfoErrorCode;
import com.codeit.findex.common.error.errorcode.SyncJobErrorCode;
import com.codeit.findex.common.error.exception.IndexInfoException;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

        List<IndexInfo> indexInfoList = indexInfoRepository.findAll();
        List<SyncJobDto> syncJobDtoList = indexInfoList.parallelStream()
                .flatMap(indexInfo -> fetchAndStoreIndexInfo(indexInfo.getIndexName(), ip).stream())
                .toList();


        return syncJobDtoList;
    }

    public List<SyncJobDto> syncIndexData(IndexDataSyncRequest request, String ip) {
        int pageSize = 1000;
        int numOfRows = 1000;

        List<Long> indexIds = request.indexInfoIds();

        if (indexIds == null || indexIds.isEmpty()) {
            return fetchAndStoreIndexData(null, request.baseDateFrom(), request.baseDateTo(), ip);
        }

        return indexIds.parallelStream()
                .map(indexId -> {
                    Optional<IndexInfo> info = indexInfoRepository.findById(indexId);
                    if (info.isEmpty()) {
                        throw new IndexInfoException(IndexInfoErrorCode.INDEX_INFO_REQUIRED_FIELD_MISSING);
                    }
                    String indexName = info.get().getIndexName();
                    return fetchAndStoreIndexData(indexName, request.baseDateFrom(), request.baseDateTo(), ip);
                })
                .flatMap(Collection::stream)
                .toList();

//        List<SyncJobDto> syncJobDtoList = new ArrayList<>();
//
//
//
//        if (request.indexInfoIds().isEmpty()) {
//            syncJobDtoList.addAll(fetchAndStoreIndexData(null, request.baseDateFrom(), request.baseDateTo(), numOfRows, pageSize, ip));
//        } else {
//            for(Long indexId : request.indexInfoIds()) {
//                if (!indexInfoRepository.existsById(indexId)) {
//                    throw new SyncJobException(SyncJobErrorCode.ASYNC_JOB_INVALID_REQUEST);
//                }
//                String indexName = indexInfoRepository.findById(indexId).get().getIndexName();
//                syncJobDtoList.addAll(fetchAndStoreIndexData(indexName, request.baseDateFrom(), request.baseDateTo(), numOfRows, pageSize, ip));
//            }
//        }
//
//        return syncJobDtoList;
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

    private List<SyncJobDto> fetchAndStoreIndexInfo(String indexName, String ip) {
        int pageNo = 1;
        int pageSize = 1000;
        int numOfRows = 1000;
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
                if (pageNo == 1) {
                    totalPages = (int) Math.ceil(body.getTotalCount() / (double) pageSize);
                }

                List<Item> items = indexApiParser.parseItems(responseJson);

                syncJobDtoList.addAll(dataSyncRepository.storeIndexInfoToDb(items, ip).stream()
                        .filter(job -> job.getJobType().equals("INDEX_INFO"))
                        .map(syncJobMapper::toDto).toList());

                pageNo++;
            } catch (InterruptedException e) {
                System.err.println("지수 정보 동기화 실패: indexName - " + indexName + ", pageNo - " + pageNo);
                e.printStackTrace();
                break;
            }
        }
        return syncJobDtoList;
    }


    private List<SyncJobDto> fetchAndStoreIndexData(String indexName, LocalDate baseDateFrom, LocalDate baseDateTo, String ip) {
        int pageNo = 1;
        int pageSize = 1000;
        int numOfRows = 1000;
        int totalPages = Integer.MAX_VALUE;
        List<SyncJobDto> syncJobDtoList = new ArrayList<>();

        while (pageNo <= totalPages) {
            try {
                StringBuilder urlBuilder = (indexName == null)
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

                syncJobDtoList.addAll(dataSyncRepository.storeIndexDataToDb(items, ip).stream()
                        .map(syncJobMapper::toDto).toList());
//                        .filter(data -> data.getJobType().equals("INDEX_DATA"))
//                        .map(syncJobMapper::toDto).toList());

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
