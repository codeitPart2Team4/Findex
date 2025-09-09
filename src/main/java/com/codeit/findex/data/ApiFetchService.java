package com.codeit.findex.data;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
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

    @PostConstruct
    public void init() throws Exception {
        System.out.println("fetchAndProcessAll 시작");
        fetchAndProcessAll();
    }

    private final FindexApiParser findexApiParser = new FindexApiParser();

    public void fetchAndProcessAll() throws Exception {
        int pageNo = 1;
        int pageSize = 100;
        int totalPages = Integer.MAX_VALUE;

        while (pageNo <= totalPages) {
            StringBuilder urlBuilder = buildUrl(pageNo, pageSize);
            String responseJson = callApi(urlBuilder);
            System.out.println("API 호출 시도: page=" + pageNo);
            if(responseJson == null || responseJson.isEmpty()) {
                System.out.println("responseJson에 문제 발생.");
                break;
            }
            System.out.println("API 응답 없음 또는 호출 실패");

            System.out.println("파싱 시작 - 전체 응답 문자열: " + responseJson);
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

    private StringBuilder buildUrl(int pageNo, int pageSize) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?serviceKey=").append(apiKey);
        urlBuilder.append("&pageNo=").append(pageNo);
        urlBuilder.append("&pageSize=").append(pageSize);
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
            if(code != 200) {
                System.out.println("요청에 실패하였습니다.");
                return null;
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

    private void processItems(int pageNo, List<Item> items) {
        for (Item item : items) {
            System.out.println("---- Item ----");
            System.out.println("pageNo: " + pageNo + ", Index Name: " + item.getIdxNm());
            System.out.println("Base Date: " + item.getBasDt());
            System.out.println("Closing Price: " + item.getClpr());
            System.out.println("Opening Price: " + item.getMkp());
            System.out.println("High Price: " + item.getHipr());
            System.out.println("Low Price: " + item.getLopr());
            System.out.println("Volume: " + item.getTrqu());
            System.out.println("Trading Amount: " + item.getTrPrc());
            System.out.println("--------------------");
        }
    }
}

