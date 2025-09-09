package com.codeit.findex.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Body {
    private int numOfRows;
    private int pageNo;
    private int totalCount;
    private Items items;
}
