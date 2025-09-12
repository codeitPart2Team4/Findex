package com.codeit.findex.syncjob.mapper;

import com.codeit.findex.syncjob.dto.SyncJobDto;
import com.codeit.findex.syncjob.entity.SyncJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncJobMapper {

    @Mapping(target = "indexInfoId", source = "indexInfo.id")
    SyncJobDto toDto(SyncJob syncJob);
}
