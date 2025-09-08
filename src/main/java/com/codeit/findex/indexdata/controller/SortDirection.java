package com.codeit.findex.indexdata.controller;

import java.util.Locale;

public enum SortDirection { asc, desc;
  public static SortDirection from(String v) {
    if (v == null || v.isBlank()) return desc;
    return SortDirection.valueOf(v.trim().toLowerCase(Locale.ROOT));
  }
}
