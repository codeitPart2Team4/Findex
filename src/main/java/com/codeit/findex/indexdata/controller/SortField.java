package com.codeit.findex.indexdata.controller;

import java.util.Locale;

public enum SortField {
  baseDate, marketPrice, closingPrice, highPrice, lowPrice,
  versus, fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount;

  public static SortField from(String v) {
    if (v == null || v.isBlank()) return baseDate;
    return SortField.valueOf(v.trim().toLowerCase(Locale.ROOT));
  }
}