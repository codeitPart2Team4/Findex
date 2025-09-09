package com.codeit.findex.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Item {
    private Double lsYrEdVsFltRt;    // 지수의 전년말 대비 등락율

    private String basPntm;          // 지수를 산출하기 위한 기준 시점
    private Double basIdx;           // 기준 시점의 지수값

    private LocalDate basDt;            // 기준일자
    private String idxCsf;           // 지수의 분류명칭
    private String idxNm;            // 지수의 명칭
    private Integer epyItmsCnt;          // 지수가 채용한 종목 수
    private Double clpr;             // 정규시장의 매매시간 종료시까지 형성되는 최종가격 (종가)
    private Double vs;               // 전일 대비 등락
    private Double fltRt;            // 전일 대비 등락에 따른 비율 (등락률)
    private Double mkp;              // 정규시장의 매매시간 개시 후 형성되는 최초 가격 (시가)
    private Double hipr;             // 하루 중 지수의 최고치 (고가)
    private Double lopr;             // 하루 중 지수의 최저치 (저가)
    private Long trqu;               // 지수에 포함된 종목의 거래량 총합
    private Long trPrc;              // 지수에 포함된 종목의 거래대금 총합
    private Long lstgMrktTotAmt;     // 지수에 포함된 종목의 시가총액
    private Integer lsYrEdVsFltRg;       // 지수의 전년말 대비 등락폭
    private Double yrWRcrdHgst;      // 지수의 연중 최고치
    private String yrWRcrdHgstDt;    // 지수가 연중 최고치를 기록한 날짜
    private Integer yrWRcrdLwst;         // 지수의 연중 최저치
    private String yrWRcrdLwstDt;    // 지수가 연중 최저치를 기록한 날짜
}
