package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WsDto<T> {

    private List<T> content;
    private int page;
    private int sizePerPage;
    private int totalPages;
    private long totalRecords;

}
