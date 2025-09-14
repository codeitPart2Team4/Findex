package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  Optional<IndexData> findByIndexInfoAndBaseDate(IndexInfo indexInfo, LocalDate basDt);

  @Modifying(clearAutomatically = true)
  @Query("delete from IndexData d where d.indexInfo.id = :indexInfoId")
  void deleteByIndexInfoId(@Param("indexInfoId") Long indexInfoId);
}