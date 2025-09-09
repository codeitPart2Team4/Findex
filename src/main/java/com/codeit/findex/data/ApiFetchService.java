package com.codeit.findex.data;

import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ApiFetchService {
    @Value("${findex.api.key}")
    private String apiKey;

    @Value("${findex.api.url}")
    private String apiUrl;

    private int count = 1;
    private final int MAX_RETRY = 3;

    @PostConstruct
    public void init() throws Exception {
        System.out.println("fetchAndProcessAll 시작");
        fetchAndProcessAll();
    }

    private final FindexApiParser findexApiParser = new FindexApiParser();

    public void fetchAndProcessAll() throws Exception {
        int pageNo = 1;
        int pageSize = 1000;
        int totalPages = Integer.MAX_VALUE;
        int numOfRows = 1000;

        while (pageNo <= totalPages) {
            StringBuilder urlBuilder = buildUrl(pageNo, numOfRows);
            String responseJson = callApiWithRetry(urlBuilder);
            System.out.println("API 호출 시도: page=" + pageNo);
            if(responseJson == null || responseJson.isEmpty()) {
                System.out.println("responseJson에 문제 발생.");
                break;
            }
            System.out.println("API 호출 성공, 응답 길이: " + responseJson.length());

            Body body = findexApiParser.parseBody(responseJson);
            System.out.println("총 데이터 수: " + body.getTotalCount());

            if (pageNo == 1) {
                totalPages = (int) Math.ceil(body.getTotalCount() / (double) pageSize);
            }

            List<Item> items = findexApiParser.parseItems(responseJson);

            System.out.println("processItems 호출 - 아이템 수: " + items.size());
            processItems(pageNo, items);

            pageNo++;

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

    private void processItems(int pageNo, List<Item> items) {
        for (Item item : items) {
            System.out.println("---- Item ----");
            System.out.println("pageNo: " + pageNo + ", count : "+ count +  ", Index Name: " + item.getIdxNm());
            System.out.println("Base Date: " + item.getBasDt());
            System.out.println("Closing Price: " + item.getClpr());
            System.out.println("Opening Price: " + item.getMkp());
            System.out.println("High Price: " + item.getHipr());
            System.out.println("Low Price: " + item.getLopr());
            System.out.println("Volume: " + item.getTrqu());
            System.out.println("Trading Amount: " + item.getTrPrc());
            System.out.println("--------------------");
            count++;
        }
    }
}

