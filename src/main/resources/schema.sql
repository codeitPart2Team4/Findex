-- ===============================
-- Table: IndexInfo (지수 정보)
-- ===============================
CREATE TABLE index_info (
                            id BIGSERIAL PRIMARY KEY,                       -- PK, 자동 증가
                            index_classification VARCHAR(100) NOT NULL,     -- 지수 분류명
                            index_name VARCHAR(100) NOT NULL UNIQUE,        -- 지수명, 유니크
                            employed_items_count INT,                       -- 채용 종목 수
                            base_point_in_time DATE,                        -- 기준 시점
                            base_index DECIMAL,                             -- 기준 지수
                            source_type VARCHAR(20) NOT NULL,               -- 소스 타입 (사용자, Open API)
                            favorite BOOLEAN DEFAULT FALSE                  -- 즐겨찾기 여부
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

-- ===============================
-- Table: AutoSync (자동 연동 설정)
-- ===============================
CREATE TABLE auto_sync (
                           id BIGSERIAL PRIMARY KEY,                            -- PK, 자동 증가
                           index_info_id BIGINT NOT NULL UNIQUE,                -- FK -> index_info.id (Unique 보장)
                           enabled BOOLEAN NOT NULL DEFAULT FALSE,              -- 활성화 여부
                           CONSTRAINT fk_auto_sync_info FOREIGN KEY (index_info_id) REFERENCES index_info(id)
);