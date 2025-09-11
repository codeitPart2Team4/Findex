package com.codeit.findex.data;

import com.codeit.findex.data.dto.Body;
import com.codeit.findex.data.dto.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ApiDataDBService {

    private final DataSyncRepository dataSyncRepository;

    private final IndexApiParser indexApiParser;

    private final int MAX_RETRY = 3;

//    @PostConstruct
//    public void init() throws Exception {
//        System.out.println("fetchAndProcessAll 시작");
//        fetchAndProcessAll();
//    }

    public void fetchAndProcessAll() {
        int pageNo = 1;
        int pageSize = 1000;
        int totalPages = Integer.MAX_VALUE;
        int numOfRows = 1000;

        while (pageNo <= totalPages) {
            try {
                StringBuilder urlBuilder = dataSyncRepository.createUrl(pageNo, numOfRows);
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
            } catch (Exception e) {
                System.err.println("동기화 실패: ");
            }

        }
    }

}

