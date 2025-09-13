package com.codeit.findex.data;

import com.codeit.findex.autosync.entity.AutoSyncConfig;
import com.codeit.findex.autosync.repository.AutoSyncConfigRepository;
import com.codeit.findex.autosync.service.AutoSyncConfigService;
import com.codeit.findex.common.enums.SourceType;
import com.codeit.findex.data.dto.Body;
import com.codeit.findex.data.dto.Item;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.syncjob.entity.SyncJob;
import com.codeit.findex.syncjob.repository.SyncJobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AutoIndexDataSyncService {

    private SyncJobRepository syncJobRepository;

    private AutoSyncConfigRepository autoSyncConfigRepository;

    private final DataSyncRepository dataSyncRepository;

    private final IndexApiParser indexApiParser;

    private final int MAX_RETRY = 3;

    @Transactional
    public void fetchAndProcessAll() throws Exception {
        int pageNo = 1;
        int pageSize = 1000;
        int totalPages = Integer.MAX_VALUE;
        int numOfRows = 1000;

        List<AutoSyncConfig> enabledTrueList = autoSyncConfigRepository.findByEnabledTrue();

        if (!enabledTrueList.isEmpty()) {
            for(AutoSyncConfig autoSyncConfig : enabledTrueList){

                IndexInfo trueIndexInfo = autoSyncConfig.getIndexInfo();

                Optional<SyncJob> trueSyncJob = syncJobRepository.findByIndexInfoAndJobType(trueIndexInfo, "INDEX_INFO");

                String beginBasDt = trueSyncJob.map(syncJob -> syncJob.getJobTime().toLocalDate().toString())
                        .orElseGet(() -> trueIndexInfo.getBasePointInTime().toString());

                System.out.println("자동 연동 업데이트: autoId - " + autoSyncConfig.getId() +
                        ", 지수 - " + trueIndexInfo.getIndexName() +
                        ", 지수 id - " + trueIndexInfo.getId() +
                        ", 최근 연동 시간 - " + beginBasDt);
                try {
                    pageNo = 1;
                    totalPages = Integer.MAX_VALUE;
                    while (pageNo <= totalPages) {
                        StringBuilder urlBuilder = dataSyncRepository.createUrl(pageNo, numOfRows, trueIndexInfo.getIndexName(), beginBasDt);
                        System.out.println("요청 url :" +  urlBuilder.toString());
                        String responseJson = dataSyncRepository.callApiWithRetry(urlBuilder);
                        System.out.println("API 호출 시도: page=" + pageNo);
                        if(responseJson == null || responseJson.isEmpty()) {
                            System.out.println("responseJson에 문제 발생.");
                            break;
                        }
                        System.out.println("API 호출 성공, 응답 길이: " + responseJson.length());

                        Body body = indexApiParser.parseBody(responseJson);
                        System.out.println("총 데이터 수: " + body.getTotalCount());

                        if (pageNo == 1) {
                            totalPages = (int) Math.ceil(body.getTotalCount() / (double) pageSize);
                        }

                        List<Item> items = indexApiParser.parseItems(responseJson);

                        System.out.println("processItems 호출 - 아이템 수: " + items.size());
                        dataSyncRepository.storeItemsToDb(items);

                        pageNo++;

                    }
                } catch (Exception e) {
                    System.err.println("동기화 실패: " + autoSyncConfig.getIndexInfo().getIndexName());
                    e.printStackTrace();
                    continue;
                }
                System.out.println("자동 연동 업데이트 완료: autoId - " + autoSyncConfig.getId() +
                        ", 지수 - " + trueIndexInfo.getIndexName() +
                        ", 지수 id - " + trueIndexInfo.getId() +
                        ", 최근 연동 시간 - " + beginBasDt);
            }
        }

    }

}

