package com.codeit.findex.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class FindexApiParser {

    public Body parseBody(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject response = jsonObject.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");

        Integer numOfRows = body.getInt("numOfRows");
        Integer pageNo = body.getInt("pageNo");
        Integer totalCount = body.getInt("totalCount");

        Body bodyObj = new Body(numOfRows, pageNo, totalCount, null);
        return bodyObj;
    }

    public List<Item> parseItems(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject response = jsonObject.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");

        int numOfRows = body.getInt("numOfRows");
        int pageNo = body.getInt("pageNo");
        int totalCount = body.getInt("totalCount");

        System.out.println("numOfRows: " + numOfRows);
        System.out.println("pageNo: " + pageNo);
        System.out.println("totalCount: " + totalCount);

        JSONObject items = body.getJSONObject("items");
        JSONArray itemArray = items.getJSONArray("item");

        List<Item> itemList = new ArrayList<>();
        for(int i = 0; i < itemArray.length(); i++) {
            JSONObject obj = itemArray.getJSONObject(i);
            Double lsYrEdVsFltRt = getDoubleData(obj, "lsYrEdVsFltRt");
            String basPntm = getStrData(obj, "basPntm");
            Double basIdx = getDoubleData(obj, "basIdx");
            LocalDate basDt = getLocalDateData(obj, "basDt");
            String idxCsf = getStrData(obj, "idxCsf");
            String idxNm = getStrData(obj, "idxNm");
            Integer epyItmsCnt = getIntData(obj, "epyItmsCnt");
            Double clpr = getDoubleData(obj, "clpr");
            Double vs = getDoubleData(obj, "vs");
            Double fltRt = getDoubleData(obj, "fltRt");
            Double mkp = getDoubleData(obj, "mkp");
            Double hipr = getDoubleData(obj, "hipr");
            Double lopr = getDoubleData(obj, "lopr");
            Long trqu = getLongData(obj, "trqu");
            Long trPrc = getLongData(obj, "trPrc");
            Long lstgMrktTotAmt = getLongData(obj, "lstgMrktTotAmt");
            Integer lsYrEdVsFltRg = getIntData(obj, "lsYrEdVsFltRg");
            Double yrWRcrdHgst = getDoubleData(obj, "yrWRcrdHgst");
            String yrWRcrdHgstDt = getStrData(obj, "yrWRcrdHgstDt");
            Integer yrWRcrdLwst = getIntData(obj, "yrWRcrdLwst");
            String yrWRcrdLwstDt = getStrData(obj, "yrWRcrdLwstDt");

            Item item = new Item(
                    lsYrEdVsFltRt, basPntm, basIdx, basDt, idxCsf, idxNm, epyItmsCnt, clpr, vs, fltRt,
                    mkp, hipr, lopr, trqu, trPrc, lstgMrktTotAmt, lsYrEdVsFltRg, yrWRcrdHgst, yrWRcrdHgstDt,
                    yrWRcrdLwst, yrWRcrdLwstDt
            );
            itemList.add(item);
        }
        return itemList;
    }

    private String getStrData(JSONObject obj, String key) {
        try {
            return obj.optString(key, null);
        } catch (Exception e) {
            return null;
        }
    }

    private Long getLongData(JSONObject obj, String key) {
        try {
            String str = obj.optString(key, null);
            if(str == null) return null;
            return Long.parseLong(str);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIntData(JSONObject obj, String key) {
        try {
            String str = obj.optString(key, null);
            if(str == null) return null;
            return Integer.parseInt(str);
        } catch (Exception e) {
            return null;
        }
    }

    private Double getDoubleData(JSONObject obj, String key) {
        try {
            String str = obj.optString(key, null);
            if(str == null) return null;
            return Double.parseDouble(str);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getLocalDateData(JSONObject obj, String key) {
        try {
            String strDate = obj.optString(key, null);
            if(strDate == null) return null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(strDate, formatter);
        } catch (Exception e) {
            return null;
        }
    }
}
