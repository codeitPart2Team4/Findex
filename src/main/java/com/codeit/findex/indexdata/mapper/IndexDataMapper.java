package com.codeit.findex.indexdata.mapper;

import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

  // === Entity -> DTO ===
  @Mappings({
      @Mapping(target = "indexInfoId", source = "indexInfo.id"),
      @Mapping(target = "sourceType",
          expression = "java(e.getSourceType()==null? null : e.getSourceType().name())")
  })
  IndexDataDto toDto(IndexData e);

  List<IndexDataDto> toDtoList(List<IndexData> list);

  // === Create DTO -> Entity ===
  default IndexData fromCreate(IndexDataCreateRequest r) {
    IndexData e = new IndexData();
    e.setBaseDate(r.baseDate());
    e.setMarketPrice(r.marketPrice());
    e.setClosingPrice(r.closingPrice());
    e.setHighPrice(r.highPrice());
    e.setLowPrice(r.lowPrice());
    e.setVersus(r.versus());
    e.setFluctuationRate(r.fluctuationRate());
    e.setTradingQuantity(r.tradingQuantity());
    e.setTradingPrice(r.tradingPrice());
    e.setMarketTotalAmount(r.marketTotalAmount());
    return e;
  }

  // === Update DTO -> Entity ===
  default void patch(IndexData e, IndexDataUpdateRequest r) {
    if (r.marketPrice() != null)       e.setMarketPrice(r.marketPrice());
    if (r.closingPrice() != null)      e.setClosingPrice(r.closingPrice());
    if (r.highPrice() != null)         e.setHighPrice(r.highPrice());
    if (r.lowPrice() != null)          e.setLowPrice(r.lowPrice());
    if (r.versus() != null)            e.setVersus(r.versus());
    if (r.fluctuationRate() != null)   e.setFluctuationRate(r.fluctuationRate());
    if (r.tradingQuantity() != null)   e.setTradingQuantity(r.tradingQuantity());
    if (r.tradingPrice() != null)      e.setTradingPrice(r.tradingPrice());
    if (r.marketTotalAmount() != null) e.setMarketTotalAmount(r.marketTotalAmount());
  }
}