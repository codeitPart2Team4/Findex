drop table index_info cascade ;
drop table index_data cascade ;
drop table sync_job  cascade ;

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

CREATE TABLE IF NOT EXISTS index_info (
                                          id BIGSERIAL PRIMARY KEY,
                                          index_classification VARCHAR(100) NOT NULL,        -- 예: KOSPI시리즈 / KRX시리즈 / KOSDAQ시리즈 / 테마지수 / 주가지수 / 업종지수
                                          index_name           VARCHAR(200) NOT NULL UNIQUE, -- 지수명(단독 유니크)
                                          employed_items_count INT,
                                          base_point_in_time   DATE,
                                          base_index           DOUBLE PRECISION,
                                          source_type          VARCHAR(20) NOT NULL,         -- 'USER' | 'OPEN_API'
                                          favorite             BOOLEAN NOT NULL DEFAULT FALSE,
                                          auto_sync_enabled    BOOLEAN NOT NULL DEFAULT FALSE,

                                          CONSTRAINT chk_index_info_source_type
                                              CHECK (source_type IN ('USER','OPEN_API')),

    -- 드롭다운에서 쓰는 분류값들을 허용
                                          CONSTRAINT chk_index_info_classification
                                              CHECK (index_classification IN ('주가지수','업종지수','테마지수','KOSPI시리즈','KOSDAQ시리즈','KRX시리즈'))
);

-- 조회/검색 성능
CREATE INDEX IF NOT EXISTS idx_index_info__class_name
    ON index_info (index_classification, index_name);

WITH idx AS (
    SELECT id FROM index_info WHERE index_name = 'KRX300'
)
INSERT INTO index_data
(index_info_id, base_date, source_type,
 market_price, closing_price, high_price, low_price,
 versus, fluctuation_rate, trading_quantity, trading_price, market_total_amount)
SELECT
    idx.id, DATE '2025-09-08', 'OPEN_API',
    1218.33, 1220.33, 1224.33, 1216.33,
    0.00, 0.00, 90000000, 4500000000000, 90000000000000
FROM idx
ON CONFLICT ON CONSTRAINT uk_index_info_base_date DO NOTHING;

WITH d AS (
    SELECT generate_series(DATE '2025-09-03', DATE '2025-09-09', INTERVAL '1 day')::date AS base_date
),
     g AS (
         SELECT i.id AS index_info_id, i.base_index, d.base_date
         FROM index_info i CROSS JOIN d
     ),
     p AS (
         SELECT
             index_info_id,
             base_date,
             'OPEN_API'::varchar AS source_type,
             ROUND( (base_index * (1 + (random()-0.5)*0.015))::numeric, 2 ) AS closing_price,
             ROUND( (base_index * (1 + (random()-0.5)*0.015))::numeric, 2 ) AS market_price
         FROM g
     ),
     calc AS (
         SELECT
             index_info_id,
             base_date,
             source_type,
             market_price,
             closing_price,
             GREATEST(market_price, closing_price) + ROUND( (random()*5)::numeric, 2 ) AS high_price,
             LEAST(market_price,  closing_price)  - ROUND( (random()*5)::numeric, 2 ) AS low_price,
             LAG(closing_price) OVER (PARTITION BY index_info_id ORDER BY base_date) AS prev_close
         FROM p
     ),
     final AS (
         SELECT
             index_info_id,
             base_date,
             source_type,
             market_price,
             closing_price,
             high_price,
             low_price,
             ROUND(COALESCE(closing_price - prev_close, 0)::numeric, 2) AS versus,
             ROUND(
                     CASE WHEN prev_close IS NULL OR prev_close = 0 THEN 0
                          ELSE ((closing_price - prev_close) / prev_close * 100)::numeric
                         END, 2
             ) AS fluctuation_rate,
             (50000000 + floor(random()*120000000))::bigint AS trading_quantity,
             (6000000000000 + floor(random()*3000000000000))::bigint AS trading_price,
             (70000000000000 + floor(random()*40000000000000))::bigint AS market_total_amount
         FROM calc
     )
INSERT INTO index_data
(index_info_id, base_date, source_type,
 market_price, closing_price, high_price, low_price,
 versus, fluctuation_rate, trading_quantity, trading_price, market_total_amount)
SELECT * FROM final
ON CONFLICT ON CONSTRAINT uk_index_info_base_date DO UPDATE
    SET market_price        = EXCLUDED.market_price,
        closing_price       = EXCLUDED.closing_price,
        high_price          = EXCLUDED.high_price,
        low_price           = EXCLUDED.low_price,
        versus              = EXCLUDED.versus,
        fluctuation_rate    = EXCLUDED.fluctuation_rate,
        trading_quantity    = EXCLUDED.trading_quantity,
        trading_price       = EXCLUDED.trading_price,
        market_total_amount = EXCLUDED.market_total_amount;

-- 전체 건수
SELECT COUNT(*) FROM index_data;

-- KRX300 최근 10일
SELECT i.index_name, d.base_date, d.closing_price
FROM index_data d
         JOIN index_info i ON i.id = d.index_info_id
WHERE i.index_name = 'KRX300'
ORDER BY d.base_date DESC
LIMIT 10;

SELECT i.index_name, COUNT(*) cnt, MIN(d.base_date) min_dt, MAX(d.base_date) max_dt
FROM index_data d
         JOIN index_info i ON i.id = d.index_info_id
WHERE i.index_name = 'KRX300'
GROUP BY i.index_name;

ALTER TABLE index_data
    ADD CONSTRAINT uk_index_info_base_date UNIQUE (index_info_id, base_date);

CREATE INDEX IF NOT EXISTS idx_index_data__ix_date
    ON index_data (index_info_id, base_date DESC);