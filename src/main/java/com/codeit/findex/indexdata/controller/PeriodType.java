package com.codeit.findex.indexdata.controller;

public enum PeriodType {
  DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY;

  public static PeriodType from(String v) {
    if (v == null) return DAILY;
    try {
      return PeriodType.valueOf(v.toUpperCase());
    } catch (IllegalArgumentException e) {
      return DAILY;
    }
  }
}