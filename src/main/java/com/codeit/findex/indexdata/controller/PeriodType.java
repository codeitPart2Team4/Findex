package com.codeit.findex.indexdata.controller;

import java.util.Locale;

public enum PeriodType { DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY;
  public static PeriodType from(String v) {
    if (v == null || v.isBlank()) return DAILY;
    return PeriodType.valueOf(v.trim().toUpperCase(Locale.ROOT));
  }
}