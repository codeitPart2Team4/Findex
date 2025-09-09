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

-- 기존 데이터 비우기(원하면)
-- TRUNCATE TABLE index_data;

-- 중복 방지 (지수+날짜 유니크)
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_indexes
            WHERE schemaname = 'public' AND indexname = 'ux_index_data_info_date'
        ) THEN
            CREATE UNIQUE INDEX ux_index_data_info_date
                ON index_data(index_info_id, base_date);
        END IF;
    END $$;

-- ===============================
-- IndexData 샘플 (id=1: 코스피)
-- ===============================
INSERT INTO index_data
(index_info_id, base_date, source_type,
 market_price, closing_price, high_price, low_price,
 versus, fluctuation_rate, trading_quantity, trading_price, market_total_amount)
VALUES
-- 2024-09-01 ~ 2024-09-07
(1,'2024-09-01','OPEN_API',2500.10,2505.00,2516.00,2490.50,  5.00,  0.20, 350000000,  9500000000000, 2000000000000),
(1,'2024-09-02','OPEN_API',2506.20,2512.30,2520.00,2501.00,  7.30,  0.29, 360000000,  9700000000000, 2005000000000),
(1,'2024-09-03','OPEN_API',2510.00,2508.10,2515.70,2498.50, -4.20, -0.17, 355000000,  9600000000000, 1999000000000),
(1,'2024-09-04','OPEN_API',2507.90,2520.40,2523.80,2504.20, 12.30,  0.49, 365000000,  9800000000000, 2010000000000),
(1,'2024-09-05','OPEN_API',2519.80,2523.10,2530.50,2512.00,  2.70,  0.11, 370000000,  9850000000000, 2013000000000),
(1,'2024-09-06','OPEN_API',2522.00,2517.60,2525.20,2511.30, -5.50, -0.22, 340000000,  9400000000000, 2007000000000),
(1,'2024-09-07','OPEN_API',2516.00,2528.90,2534.00,2510.50, 11.30,  0.45, 380000000, 10000000000000, 2020000000000);

-- ===============================
-- IndexData 샘플 (id=2: 코스닥)
-- ===============================
INSERT INTO index_data
(index_info_id, base_date, source_type,
 market_price, closing_price, high_price, low_price,
 versus, fluctuation_rate, trading_quantity, trading_price, market_total_amount)
VALUES
    (2,'2024-09-01','OPEN_API', 820.20, 822.50, 826.00, 818.00,  2.30,  0.28, 120000000,  1500000000000,  350000000000),
    (2,'2024-09-02','OPEN_API', 822.00, 819.40, 823.50, 816.50, -3.10, -0.38, 115000000,  1450000000000,  348000000000),
    (2,'2024-09-03','OPEN_API', 819.00, 825.20, 827.80, 818.10,  5.80,  0.71, 125000000,  1520000000000,  353000000000),
    (2,'2024-09-04','OPEN_API', 826.00, 828.30, 830.50, 823.40,  3.10,  0.38, 130000000,  1550000000000,  356000000000),
    (2,'2024-09-05','OPEN_API', 828.00, 826.10, 829.40, 824.00, -2.20, -0.27, 118000000,  1480000000000,  354000000000),
    (2,'2024-09-06','OPEN_API', 826.50, 832.40, 834.20, 825.30,  6.30,  0.76, 135000000,  1600000000000,  358000000000),
    (2,'2024-09-07','OPEN_API', 832.00, 831.10, 835.00, 829.20, -1.30, -0.16, 110000000,  1420000000000,  357000000000);

-- ===============================
-- IndexData 샘플 (id=3: 코스피200)
-- ===============================
INSERT INTO index_data
(index_info_id, base_date, source_type,
 market_price, closing_price, high_price, low_price,
 versus, fluctuation_rate, trading_quantity, trading_price, market_total_amount)
VALUES
    (3,'2024-09-01','OPEN_API', 330.10, 331.20, 332.30, 329.50,  1.10,  0.33,  45000000,  580000000000, 120000000000),
    (3,'2024-09-02','OPEN_API', 331.00, 330.40, 331.90, 329.80, -0.80, -0.24,  42000000,  560000000000, 119500000000),
    (3,'2024-09-03','OPEN_API', 330.50, 332.10, 333.20, 330.10,  1.70,  0.51,  47000000,  600000000000, 121000000000),
    (3,'2024-09-04','OPEN_API', 332.00, 333.80, 334.40, 331.50,  1.70,  0.51,  49000000,  620000000000, 121800000000),
    (3,'2024-09-05','OPEN_API', 333.50, 334.10, 335.10, 332.90,  0.30,  0.09,  43000000,  555000000000, 122000000000),
    (3,'2024-09-06','OPEN_API', 334.00, 333.00, 334.60, 332.20, -1.10, -0.33,  41000000,  530000000000, 121500000000),
    (3,'2024-09-07','OPEN_API', 333.10, 335.50, 336.20, 332.80,  2.50,  0.75,  52000000,  640000000000, 122800000000);

-- 확인
SELECT index_info_id, COUNT(*) AS rows_per_index
FROM index_data
GROUP BY index_info_id
ORDER BY index_info_id;

SELECT id, index_name FROM index_info ORDER BY id;