package com.codeit.findex.data;

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
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class ApiDataDBService {
    @Value("${findex.api.key}")
    private String apiKey;

    @Value("${findex.api.url}")
    private String apiUrl;

    private final int MAX_RETRY = 3;

    @Autowired
    private IndexInfoRepository indexInfoRepository;

    @Autowired
    private IndexDataRepository indexDataRepository;

    @Autowired
    private SyncJobRepository syncJobRepository;

//    @PostConstruct
//    public void init() throws Exception {
//        System.out.println("fetchAndProcessAll 시작");
//        fetchAndProcessAll();
//    }

    private final IndexApiParser indexApiParser = new IndexApiParser();

    public void fetchAndProcessAll() {
        int pageNo = 1;
        int pageSize = 1000;
        int totalPages = Integer.MAX_VALUE;
        int numOfRows = 1000;

        while (pageNo <= totalPages) {
            try {
                StringBuilder urlBuilder = buildUrl(pageNo, numOfRows);
                System.out.println("요청 url :" +  urlBuilder.toString());
                String responseJson = callApiWithRetry(urlBuilder);
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
                storeItemsToDb(items);

                pageNo++;
            } catch (Exception e) {
                System.err.println("동기화 실패: ");
            }

        }
    }

    private String callApiWithRetry(StringBuilder urlBuilder) throws InterruptedException {
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

    private StringBuilder buildUrl(int pageNo, int numOfRows) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?serviceKey=").append(apiKey);
        urlBuilder.append("&pageNo=").append(pageNo);
        urlBuilder.append("&numOfRows=").append(numOfRows);
        urlBuilder.append("&resultType=json");

        return urlBuilder;
    }

    private String callApi(StringBuilder urlBuilder) {
        try {
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            int code = conn.getResponseCode();
            System.out.println("Response code: " + code);
            if(code != HttpURLConnection.HTTP_OK) {
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
    protected void storeItemsToDb(List<Item> items) {
        Set<SyncJob> syncJobsList = new HashSet<>();
        Set<IndexData> indexDataList = new HashSet<>();

        for (Item item : items) {
            IndexInfo indexInfo = null;
            try {
                indexInfo = indexInfoRepository.findByIndexName(item.getIdxNm())
                        .orElseGet(() -> {
                            IndexInfo newInfo = new IndexInfo();
                            newInfo.setIndexClassification(item.getIdxCsf());
                            newInfo.setIndexName(item.getIdxNm());
                            newInfo.setSourceType(SourceType.OPEN_API);
                            newInfo.setBasePointInTime(item.getBasPntm());
                            newInfo.setBaseIndex(BigDecimal.valueOf(item.getBasIdx()));

                            return newInfo;
                        });
                indexInfo.setEmployedItemsCount(item.getEpyItmsCnt());

                indexInfo = indexInfoRepository.save(indexInfo);

                IndexData indexData = indexDataRepository.findByIndexInfoAndBaseDate(indexInfo, item.getBasDt())
                        .orElseGet(() -> {
                            IndexData newData = new IndexData();
                            newData.setSourceType(SourceType.OPEN_API);

                            return newData;
                        });
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


                indexDataList.add(indexData);

                SyncJob syncInfoJob = syncJobRepository.findByIndexInfoAndJobType(indexInfo, "INDEX_INFO")
                .orElseGet(() -> {
                    SyncJob newJob = new SyncJob();
                    newJob.setJobType("INDEX_INFO");
                    newJob.setWorker("system");
                    newJob.setResult("SUCCESS");

                    return newJob;
                });
                syncInfoJob.setIndexInfo(indexInfo);
                syncInfoJob.setTargetDate(item.getBasDt());
                syncJobsList.add(syncInfoJob);

                SyncJob syncDataJob = syncJobRepository.findByIndexInfoAndJobTypeAndTargetDate(indexInfo, "INDEX_DATA", item.getBasDt())
                        .orElseGet(() -> {
                            SyncJob newJob = new SyncJob();
                            newJob.setJobType("INDEX_DATA");
                            newJob.setWorker("system");
                            newJob.setResult("SUCCESS");

                            return newJob;
                        });
                syncDataJob.setIndexInfo(indexInfo);
                syncDataJob.setTargetDate(item.getBasDt());
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


}

