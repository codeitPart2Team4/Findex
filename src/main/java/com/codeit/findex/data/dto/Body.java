package com.codeit.findex.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Body {
    private int numOfRows;
    private int pageNo;
    private int totalCount;
    private Items items;
}
