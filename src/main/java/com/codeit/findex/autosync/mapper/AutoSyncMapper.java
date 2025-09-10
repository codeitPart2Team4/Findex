package com.codeit.findex.autosync.mapper;


import com.codeit.findex.autosync.dto.AutoSyncConfigDto;
import com.codeit.findex.autosync.entity.AutoSyncConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoSyncMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "indexInfo.indexName", target = "indexName")
    AutoSyncConfigDto toDto(AutoSyncConfig entity);
}
