package com.codeit.findex.data;

import com.codeit.findex.common.enums.SourceType;
import com.codeit.findex.data.dto.Item;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import com.codeit.findex.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.syncjob.entity.SyncJob;
import com.codeit.findex.syncjob.repository.SyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSyncRepository {

    @Value("${findex.api.key}") private String apiKey;

    @Value("${findex.api.url}")  private String apiUrl;

    private IndexInfoRepository indexInfoRepository;

    private IndexDataRepository indexDataRepository;

    private SyncJobRepository syncJobRepository;

    private final int MAX_RETRY = 3;


    @Transactional
    public StringBuilder createUrl(int pageNo, int numOfRows) {
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?serviceKey=").append(apiKey);
        urlBuilder.append("&pageNo=").append(pageNo);
        urlBuilder.append("&numOfRows=").append(numOfRows);
        urlBuilder.append("&resultType=json");

        return urlBuilder;
    }

    @Transactional
    public StringBuilder createUrl(int pageNo, int numOfRows, String idxNm, String beginBasDt) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?serviceKey=").append(apiKey);
        urlBuilder.append("&pageNo=").append(pageNo);
        urlBuilder.append("&numOfRows=").append(numOfRows);
        urlBuilder.append("&beginBasDt=").append(beginBasDt);
        urlBuilder.append("&idxNm=").append(URLEncoder.encode(idxNm, "UTF-8"));
        urlBuilder.append("&resultType=json");

        return urlBuilder;
    }

    @Transactional
    public String callApiWithRetry(StringBuilder urlBuilder) throws InterruptedException {
        int retryCount = 0;

        while (retryCount < MAX_RETRY) {
            String responseJson = callApi(urlBuilder);

            if(responseJson != null && !responseJson.isEmpty()) {
                if(isValidResponse(responseJson)) {
                    return responseJson;
                } else {
                    System.err.println("API 내부 오류 감지. 응답 내용 로그 출력:\n" + responseJson);
                }
            } else {
                System.err.println("빈 응답 감지. 재시도 " + (retryCount+ 1));
            }

            retryCount++;
            Thread.sleep(1000L * retryCount);

        }

        System.err.println("최대 재시도 도달. API 호출 실패.");
        return null;
    }

    private String callApi(StringBuilder urlBuilder) {
        try {
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            int code = conn.getResponseCode();
            System.out.println("Response code: " + code);
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP 오류 코드: " + code);
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidResponse(String responseJson) {
        JSONObject json = new JSONObject(responseJson);
        JSONObject header = json.getJSONObject("response").getJSONObject("header");
        String resultCode = header.optString("resultCode", "");
        String resultMsg = header.optString("resultMsg", "");

        if(!resultCode.equals("00")) {
            System.err.println("API 장애 또는 오류 반환:  코드 = " + resultCode + ", 메시지 = " +  resultMsg);
            return false;
        }
        return true;
    }

    @Transactional
    public void storeItemsToDb(List<Item> items) {
        Set<SyncJob> syncJobsList = new HashSet<>();
        Set<IndexData> indexDataList = new HashSet<>();

        for (Item item : items) {
            IndexInfo indexInfo = null;
            try {
                 indexInfo = toIndexInfo(item);
                indexInfo = indexInfoRepository.save(indexInfo);

                IndexData indexData = toIndexData(item, indexInfo);

                indexDataList.add(indexData);

                SyncJob syncInfoJob = toSyncJob(item, indexInfo, "INDEX_INFO");
                syncJobsList.add(syncInfoJob);

                SyncJob syncDataJob = toSyncJob(item, indexInfo, "INDEX_DATA");
                syncJobsList.add(syncDataJob);
            } catch (Exception e) {
                Optional<SyncJob> syncInfoFailJob = syncJobRepository.findByIndexInfoAndJobType(indexInfo, "INDEX_INFO");
                syncInfoFailJob.ifPresent(syncJob -> {syncJob.setResult("FAIL");});


                Optional<SyncJob> syncDataFailJob = syncJobRepository.findByIndexInfoAndJobTypeAndTargetDate(indexInfo, "INDEX_DATA", item.getBasDt());
                syncDataFailJob.ifPresent(syncJob -> {syncJob.setResult("FAIL");});

                e.printStackTrace();
            }
        }

        indexDataRepository.saveAll(indexDataList);
        syncJobRepository.saveAll(syncJobsList);
    }

    private IndexInfo toIndexInfo(Item item) {
        IndexInfo indexInfo = indexInfoRepository.findByIndexName(item.getIdxNm())
                .orElseGet(IndexInfo::new);

        indexInfo.setIndexClassification(item.getIdxCsf());
        indexInfo.setIndexName(item.getIdxNm());
        indexInfo.setSourceType(SourceType.OPEN_API);
        indexInfo.setBasePointInTime(item.getBasPntm());
        indexInfo.setBaseIndex(BigDecimal.valueOf(item.getBasIdx()));
        indexInfo.setEmployedItemsCount(item.getEpyItmsCnt());

        return indexInfo;
    }

    private IndexData toIndexData(Item item, IndexInfo indexInfo) {
        IndexData indexData = indexDataRepository.findByIndexInfoAndBaseDate(indexInfo, item.getBasDt())
                .orElseGet(IndexData::new);

        indexData.setSourceType(SourceType.OPEN_API);
        indexData.setIndexInfo(indexInfo);
        indexData.setBaseDate(item.getBasDt());
        indexData.setMarketPrice(item.getMkp());
        indexData.setClosingPrice(item.getClpr());
        indexData.setHighPrice(item.getHipr());
        indexData.setLowPrice(item.getLopr());
        indexData.setVersus(item.getVs());
        indexData.setFluctuationRate(item.getFltRt());
        indexData.setTradingQuantity(item.getTrqu());
        indexData.setTradingPrice(item.getTrPrc());
        indexData.setMarketTotalAmount(item.getLstgMrktTotAmt());

        return indexData;
    }

    private SyncJob toSyncJob(Item item, IndexInfo indexInfo, String jobType) {
        switch(jobType) {
            case "INDEX_INFO" -> {
                SyncJob syncInfoJob = syncJobRepository.findByIndexInfoAndJobType(indexInfo, jobType)
                        .orElseGet(SyncJob::new);
                syncInfoJob.setIndexInfo(indexInfo);
                syncInfoJob.setTargetDate(item.getBasDt());
                syncInfoJob.setJobType(jobType);
                syncInfoJob.setWorker("system");
                syncInfoJob.setResult("SUCCESS");

                return syncInfoJob;
            }
            case "INDEX_DATA" -> {
                SyncJob syncDataJob = syncJobRepository.findByIndexInfoAndJobTypeAndTargetDate(indexInfo, jobType, item.getBasDt())
                        .orElseGet(SyncJob::new);
                syncDataJob.setIndexInfo(indexInfo);
                syncDataJob.setTargetDate(item.getBasDt());
                syncDataJob.setJobType(jobType);
                syncDataJob.setWorker("system");
                syncDataJob.setResult("SUCCESS");

                return syncDataJob;
            }
            default -> throw new IllegalArgumentException("Unknown job type: " + jobType);
        }
    }
}
