package com.codeit.findex.indexinfo.mapper;

import com.codeit.findex.indexinfo.dto.IndexInfoDto;
import com.codeit.findex.indexinfo.dto.IndexInfoSummaryDto;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {

    IndexInfoDto toDto(IndexInfo indexInfo);
    IndexInfoSummaryDto toSummaryDto(IndexInfo indexInfo);
}
