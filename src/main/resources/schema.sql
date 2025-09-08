drop table index_info;

-- ===============================
-- Table: IndexInfo (지수 정보)
-- ===============================
CREATE TABLE index_info (
                            id BIGSERIAL PRIMARY KEY,                           -- PK, 자동 증가
                            index_classification VARCHAR(100) NOT NULL,         -- 지수 분류명
                            index_name VARCHAR(100) NOT NULL UNIQUE,            -- 지수명, 유니크
                            employed_items_count INT,                           -- 채용 종목 수
                            base_point_in_time DATE,                            -- 기준 시점
                            base_index DECIMAL,                                 -- 기준 지수
                            source_type VARCHAR(20) NOT NULL,                   -- 소스 타입 (사용자, Open API)
                            favorite BOOLEAN NOT NULL DEFAULT FALSE,            -- 즐겨찾기 여부
                            auto_sync_enabled BOOLEAN NOT NULL DEFAULT FALSE    -- 자동 연동 여부
);

-- ===============================
-- Table: IndexData (지수 데이터)
-- ===============================
CREATE TABLE index_data (
                            id BIGSERIAL PRIMARY KEY,                              -- PK, 자동 증가
                            index_info_id BIGINT NOT NULL,                         -- FK -> index_info.id
                            base_date DATE NOT NULL,                               -- 기준 일자
                            source_type VARCHAR(20) NOT NULL,                      -- 소스 타입 (사용자, Open API)
                            market_price DECIMAL,                                  -- 시가
                            closing_price DECIMAL,                                 -- 종가
                            high_price DECIMAL,                                    -- 고가
                            low_price DECIMAL,                                     -- 저가
                            versus DECIMAL,                                        -- 대비
                            fluctuation_rate DECIMAL,                              -- 등락률
                            trading_quantity BIGINT,                               -- 거래량
                            trading_price BIGINT,                                  -- 거래대금
                            market_total_amount BIGINT,                            -- 시가총액
                            CONSTRAINT fk_index_data_info FOREIGN KEY (index_info_id) REFERENCES index_info(id)
);

-- ===============================
-- Table: SyncJob (연동 이력)
-- ===============================
CREATE TABLE sync_job (
                          id BIGSERIAL PRIMARY KEY,                               -- PK, 자동 증가
                          index_info_id BIGINT NOT NULL,                          -- FK -> index_info.id
                          job_type VARCHAR(50) NOT NULL,                          -- 작업 유형 (INDEX_INFO, INDEX_DATA)
                          target_date DATE NOT NULL,                              -- 대상 날짜
                          worker VARCHAR(100) NOT NULL,                           -- 작업자(IP, 시스템 등)
                          job_time TIMESTAMP NOT NULL DEFAULT now(),              -- 작업 일시
                          result VARCHAR(20) NOT NULL,                            -- 결과 (NEW, SUCCESS, FAIL)
                          CONSTRAINT fk_sync_job_info FOREIGN KEY (index_info_id) REFERENCES index_info(id)
);

-- 주가지수 계열
INSERT INTO index_info (index_classification, index_name, employed_items_count, base_point_in_time, base_index, source_type, favorite, auto_sync_enabled)
VALUES
    ('주가지수', '코스피', 200, '2025-09-08', 2500.12, 'OPEN_API', false, false),
    ('주가지수', '코스닥', 150, '2025-09-08', 820.45, 'OPEN_API', true, false),
    ('주가지수', '코스피200', 200, '2025-09-08', 330.78, 'OPEN_API', false, false),
    ('주가지수', 'KRX100', 100, '2025-09-08', 510.25, 'OPEN_API', false, false),
    ('주가지수', 'KRX300', 300, '2025-09-08', 1220.33, 'OPEN_API', false, false),
    ('주가지수', 'KRX 소형주', 80, '2025-09-08', 640.12, 'OPEN_API', false, false),
    ('주가지수', 'KRX 중형주', 120, '2025-09-08', 910.54, 'OPEN_API', false, false),
    ('주가지수', 'KRX 대형주', 60, '2025-09-08', 1430.77, 'OPEN_API', true, false),
    ('주가지수', 'KRX 배당주', 40, '2025-09-08', 1120.80, 'OPEN_API', false, false),
    ('주가지수', 'KRX 가치주', 55, '2025-09-08', 875.10, 'OPEN_API', false, false);

-- 업종지수 계열
INSERT INTO index_info (index_classification, index_name, employed_items_count, base_point_in_time, base_index, source_type, favorite, auto_sync_enabled)
VALUES
    ('업종지수', 'KRX 자동차', 50, '2025-09-08', 1230.66, 'OPEN_API', true, false),
    ('업종지수', 'KRX 반도체', 60, '2025-09-08', 2780.44, 'OPEN_API', false, false),
    ('업종지수', 'KRX 헬스케어', 80, '2025-09-08', 1920.10, 'OPEN_API', false, false),
    ('업종지수', 'KRX 금융', 70, '2025-09-08', 1340.25, 'OPEN_API', false, false),
    ('업종지수', 'KRX 은행', 30, '2025-09-08', 990.55, 'OPEN_API', true, false),
    ('업종지수', 'KRX 건설', 25, '2025-09-08', 755.40, 'OPEN_API', false, false),
    ('업종지수', 'KRX 에너지', 45, '2025-09-08', 1660.85, 'OPEN_API', false, false),
    ('업종지수', 'KRX 화학', 35, '2025-09-08', 1780.12, 'OPEN_API', false, false),
    ('업종지수', 'KRX 철강', 20, '2025-09-08', 980.30, 'OPEN_API', false, false),
    ('업종지수', 'KRX 유통', 28, '2025-09-08', 1140.95, 'OPEN_API', false, false);

-- 테마지수 계열
INSERT INTO index_info (index_classification, index_name, employed_items_count, base_point_in_time, base_index, source_type, favorite, auto_sync_enabled)
VALUES
    ('테마지수', 'KRX 친환경', 15, '2025-09-08', 1350.12, 'OPEN_API', true, false),
    ('테마지수', 'KRX 2차전지', 22, '2025-09-08', 2100.50, 'OPEN_API', false, false),
    ('테마지수', 'KRX AI', 18, '2025-09-08', 1875.90, 'OPEN_API', false, false),
    ('테마지수', 'KRX 로봇', 12, '2025-09-08', 1540.75, 'OPEN_API', false, false),
    ('테마지수', 'KRX 메타버스', 16, '2025-09-08', 1190.33, 'OPEN_API', false, false),
    ('테마지수', 'KRX 클라우드', 14, '2025-09-08', 1280.60, 'OPEN_API', false, false),
    ('테마지수', 'KRX 게임', 25, '2025-09-08', 990.40, 'OPEN_API', false, false),
    ('테마지수', 'KRX 전기차', 19, '2025-09-08', 2200.75, 'OPEN_API', true, false),
    ('테마지수', 'KRX 바이오', 21, '2025-09-08', 2450.12, 'OPEN_API', false, false),
    ('테마지수', 'KRX 여행', 10, '2025-09-08', 720.90, 'OPEN_API', false, false);

select count(*) from index_info;