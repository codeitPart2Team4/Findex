# Findex

프로젝트 상세 구현 기능은 <b>노션</b>에서 확인할 수 있습니다.(<b>이미지 클릭</b>)<br>

<a href="https://findex4team.notion.site/FINDEX-26f736a08e8180ed85d7f642b6a73317">
  <img src="/src/main/resources/static/assets/Findex-logo.png" width="800" height="600" alt="Findex Logo"/>
</a>

---

## 프로젝트 소개
한눈에 보는 금융 지수 데이터!<br>
Findex는 외부 Open API와 연동하여 금융 지수 데이터를 제공하는 <b>대시보드 서비스</b>입니다.<br>
사용자는 직관적인 UI에서 금융 지수의 흐름을 파악하고, 자동 연동 기능을 통해 최신 데이터를 분석할 수 있습니다.<br>
지수별 <b>성과 분석, 이동평균선 계산, 자동 데이터 업데이트 기능</b>을 통해 가볍고 강력한 금융 분석 도구를 경험해 보세요.

---
## 팀원 소개
- 김준교(https://github.com/rlawnsry)
- 김유민(https://github.com/kimyumin03)
- 김동규(https://github.com/redmatoda)
- 임재혁(https://github.com/JaehyeokLim)
- 임승택(https://github.com/lsttsl2019)

---
## 팀원별 구현 기능 상세
### 김유민 임승택
#### 1. 대시보드 관리
- 여러 지수의 최근 성과 요약, 시계열 차트(MA5/MA20), 성과 랭킹, 상세 목록+필터/정렬, CSV 내보내기를 한 화면에서 제공
- 대량 데이터에서도 빠른 응답을 위해 커서 기반 페이지네이션과 집계 규칙(기간 마감 종가) 적용
- 즐겨찾기된 지수의 현재 종가, 전일 대비(등락/등락률) 카드형 요약
- 각 기간 버킷의 마감일 종가(대표값) + MA5/MA20 오버레이를 통한 지수 차트(시계열 + 이동평균) 구현
- 전일/전주/전월 대비 등락률 내림차순으로 성과 분석 랭킹 구현
#### 2. 지수 데이터 관리
- 지수 데이터 CRUD 기능 구현
- CSV Export 구현
- 차트(DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY) + MA5/MA20 성과 랭킹(등락률 desc, 동률 시 대비 desc) 구현

### 임재혁
#### 1. 지수 정보 관리
- 지수 정보 CRUD 기능 구현
- 대량 데이터에 대비한 커서 기반 페이지네이션 및 분류명, 지수명, 종목 수, ID 기준으로 정렬 기능 적용
- 즐겨찾기(favorite) 및 요약 조회 API 기능 구현
- Querydsl 기반 정렬 및 커서 조건 적용 방식 유지
- 기존 오프셋 기반 요소 제거 후 불필요한 Page 객체 생성 과정 제거
#### 2. 공통 에러
- 도메인별 ErrorCode 및 Exception 정의 추가
- BaseErrorCode, BaseException 기반 전역 예외 처리 인프라 구축
- GlobalExceptionHandler를 커스텀 예외 처리 구조로 리팩토링

#### 3. 배포 설정
- application.yml에서 로컬 DB 설정 제거
- Railway에서 제공하는 환경변수(DATABASE_URL, POSTGRES_USER, POSTGRES_PASSWORD)를 사용하도록 수정

### 김준교
####  1. 연동 작업 및 자동 연동 설정 관리
- API 파싱 및 DataBase에 데이터 삽입
- 지수 데이터 및 정보 연동
- 연동 작업 목록 조회 기능 구현
- API 연동 자동화 서비스(ApiAutoSyncService) 및 Scheduler 추가
- API 응답 구조(header, body, items 등)를 DTO로 정의
- 파싱 전용 모듈(FindexApiParser)과 일부 API 호출 로직 추가

### 김동규
#### 1. 자동 연동 설정 관리
- 자동 연동 설정 조회 및 수정 API 구현
- 컨트롤러에서 id 파라미터 타입 문제 해결 및 ResponseEntity 반환 타입 명확화
- 활성화 버튼 클릭 시 발생하던 빈 데이터 행 생성 문제 수정
- id 검증 로직 추가 및 유효하지 않은 id 요청에 대해 400 Bad Request 처리
---
## 기술 스택
- 언어 : Java
- 프레임워크 : Spring Boot, Tomcat
- 데이터베이스 : PostgreSQL, Spring Data JPA, QueryDSL
- 빌드 및 의존성 관리 도구 : Gradle
- 인프라 : Railway
- API 문서 : Swagger
- 기타 도구 : MapStruct, 공공데이터포털 API
---
## 구현 기능
### 1) Open API 연동
- 공공데이터포털 등 외부 Open API 연동 준비/구성 (인증키/요청 파라미터/쿼터 관리)
### 2) 지수 정보 관리
- 지수 정보(메타) 조회
- 지수 정보 등록 / 수정 / 삭제
- 지수 정보 목록 조회
### 3) 지수 데이터 관리
- 지수 데이터(시계열) 조회
- 지수 데이터 등록 / 수정 / 삭제
- 지수 데이터 목록 조회
- 지수 데이터 Export (CSV 등)
### 4) 연동 작업 관리
- 연동 작업 정보 조회
- 지수 정보 연동 (메타 싱크)
- 지수 데이터 연동 (시계열 싱크)
- 연동 작업 목록 조회
### 5) 자동 연동 설정 관리
- 자동 연동 정보/상태 조회
- 자동 연동 설정 등록 / 수정 / 목록 조회
- 배치(스케줄러)에 의한 자동 갱신
### 6) 대시보드
- 주요 지수 현황 요약
- 지수 차트 (이동평균선 등 보조지표)
- 지수 성과 분석 랭킹

---
## 구현 홈페이지
Railway를 이용하여 배포하였습니다.<br>
https://findex-production-84b9.up.railway.app/#/dashboard



---
## 프로젝트 구조

<details>
  <summary>프로젝트 구조 보기</summary>

```text
.
├── README.md
├── HELP.md
├── build.gradle
├── class-diagram.puml
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src/
    ├── main/
    │   ├── java/com/codeit/findex/
    │   │   ├── FindexApplication.java
    │   │   ├── autosync/
    │   │   │   ├── controller/AutoSyncConfigController.java
    │   │   │   ├── dto/
    │   │   │   │   ├── AutoSyncConfigDto.java
    │   │   │   │   ├── AutoSyncConfigUpdateRequest.java
    │   │   │   │   └── CursorPageResponse.java
    │   │   │   ├── entity/AutoSyncConfig.java
    │   │   │   ├── mapper/AutoSyncMapper.java
    │   │   │   ├── repository/
    │   │   │   │   ├── AutoSyncConfigQueryRepository.java
    │   │   │   │   ├── AutoSyncConfigQueryRepositoryImpl.java
    │   │   │   │   └── AutoSyncConfigRepository.java
    │   │   │   └── service/AutoSyncConfigService.java
    │   │   ├── common/
    │   │   │   ├── dto/PageResponse.java
    │   │   │   ├── entity/BaseEntity.java
    │   │   │   ├── enums/SortDirection.java
    │   │   │   ├── enums/SourceType.java
    │   │   │   └── error/
    │   │   │       ├── ErrorResponse.java
    │   │   │       ├── GlobalExceptionHandler.java
    │   │   │       ├── errorcode/
    │   │   │       │   ├── AutoSyncErrorCode.java
    │   │   │       │   ├── BaseErrorCode.java
    │   │   │       │   ├── IndexDataErrorCode.java
    │   │   │       │   ├── IndexInfoErrorCode.java
    │   │   │       │   └── SyncJobErrorCode.java
    │   │   │       └── exception/
    │   │   │           ├── AutoSyncException.java
    │   │   │           ├── BaseException.java
    │   │   │           ├── IndexDataException.java
    │   │   │           ├── IndexInfoException.java
    │   │   │           └── SyncJobException.java
    │   │   ├── config/
    │   │   │   ├── QuerydslConfig.java
    │   │   │   └── WebConfig.java
    │   │   ├── data/
    │   │   │   ├── ApiDataDBService.java
    │   │   │   ├── AutoIndexDataSyncService.java
    │   │   │   ├── DataSyncRepository.java
    │   │   │   ├── IndexApiParser.java
    │   │   │   ├── dto/
    │   │   │   │   ├── Body.java
    │   │   │   │   ├── Header.java
    │   │   │   │   ├── Item.java
    │   │   │   │   ├── Items.java
    │   │   │   │   └── Response.java
    │   │   │   └── scheduler/IndexApiScheduler.java
    │   │   ├── indexdata/
    │   │   │   ├── controller/
    │   │   │   │   ├── IndexDataApi.java
    │   │   │   │   ├── IndexDataController.java
    │   │   │   │   ├── IndexDataExtraApi.java
    │   │   │   │   ├── IndexDataExtraController.java
    │   │   │   │   ├── PeriodType.java
    │   │   │   │   └── SortField.java
    │   │   │   ├── dto/
    │   │   │   │   ├── ChartPoint.java
    │   │   │   │   ├── ChartPointDto.java
    │   │   │   │   ├── IndexChartDto.java
    │   │   │   │   ├── IndexDataCreateRequest.java
    │   │   │   │   ├── IndexDataDto.java
    │   │   │   │   ├── IndexDataUpdateRequest.java
    │   │   │   │   ├── IndexPerformanceDto.java
    │   │   │   │   ├── IndexPerformanceWithRankDto.java
    │   │   │   │   └── PerformanceRankDto.java
    │   │   │   ├── entity/IndexData.java
    │   │   │   ├── mapper/IndexDataMapper.java
    │   │   │   ├── repository/
    │   │   │   │   ├── IndexDataExtraRepository.java
    │   │   │   │   ├── IndexDataExtraRepositoryImpl.java
    │   │   │   │   ├── IndexDataQueryRepository.java
    │   │   │   │   ├── IndexDataQueryRepositoryImpl.java
    │   │   │   │   └── IndexDataRepository.java
    │   │   │   └── service/
    │   │   │       ├── IndexDataExtraService.java
    │   │   │       └── IndexDataService.java
    │   │   ├── indexinfo/
    │   │   │   ├── controller/IndexInfoController.java
    │   │   │   ├── dto/
    │   │   │   │   ├── IndexInfoCreateRequest.java
    │   │   │   │   ├── IndexInfoDto.java
    │   │   │   │   ├── IndexInfoSummaryDto.java
    │   │   │   │   └── IndexInfoUpdateRequest.java
    │   │   │   ├── entity/IndexInfo.java
    │   │   │   ├── mapper/IndexInfoMapper.java
    │   │   │   ├── repository/
    │   │   │   │   ├── IndexInfoQueryRepository.java
    │   │   │   │   ├── IndexInfoQueryRepositoryImpl.java
    │   │   │   │   └── IndexInfoRepository.java
    │   │   │   └── service/IndexInfoService.java
    │   │   └── syncjob/
    │   │       ├── controller/SyncJobController.java
    │   │       ├── dto/
    │   │       │   ├── IndexDataSyncRequest.java
    │   │       │   └── SyncJobDto.java
    │   │       ├── entity/SyncJob.java
    │   │       ├── mapper/SyncJobMapper.java
    │   │       ├── repository/
    │   │       │   ├── SyncJobQueryRepository.java
    │   │       │   ├── SyncJobQueryRepositoryImpl.java
    │   │       │   └── SyncJobRepository.java
    │   │       └── service/SyncJobService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── schema.sql
    │       └── static/
    │           ├── assets/
    │           │   ├── Findex-logo.png
    │           │   ├── index-CGZC7fCi.js
    │   │       │   └── index-Dtn62Xmo.css
    │           ├── favicon.ico
    │           └── index.html
    └── test/java/com/codeit/findex/
        └── FindexApplicationTests.java
```
</details>

---
