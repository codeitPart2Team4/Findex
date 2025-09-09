package com.codeit.findex.indexdata.controller;

public enum SortField {
  baseDate,
  marketPrice,
  closingPrice,
  highPrice,
  lowPrice,
  versus,
  fluctuationRate,
  tradingQuantity,
  tradingPrice,
  marketTotalAmount;

  public static SortField from(String v) {
    if (v == null) return baseDate;
    try {
      return SortField.valueOf(v);
    } catch (IllegalArgumentException e) {
      return baseDate;
    }
  }
}