package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IndexDataRepository
    extends JpaRepository<IndexData, Long>, IndexDataQueryRepository {

  Optional<IndexData> findByIndexInfo_IdAndBaseDate(Long indexInfoId, LocalDate baseDate);

  List<IndexData> findTop60ByIndexInfo_IdAndBaseDateBetweenOrderByBaseDateAsc(
      Long indexInfoId, LocalDate from, LocalDate to);

  Optional<IndexData> findTop1ByIndexInfo_IdAndBaseDateLessThanEqualOrderByBaseDateDesc(
      Long indexInfoId, LocalDate baseDate);
}