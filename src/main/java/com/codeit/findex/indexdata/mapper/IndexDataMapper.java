package com.codeit.findex.indexdata.mapper;

import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

  @Mappings({
      @Mapping(target = "indexInfoId", source = "indexInfo.id"),
      @Mapping(target = "indexName",   source = "indexInfo.indexName"),
      @Mapping(target = "baseDate",    source = "baseDate"),
      @Mapping(target = "sourceType",  expression = "java(e.getSourceType()==null? null : e.getSourceType().name())")
  })
  IndexDataDto toDto(IndexData e);

  List<IndexDataDto> toDtoList(List<IndexData> list);
}