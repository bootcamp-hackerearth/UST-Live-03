package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter

public class PaginationDto {
    private int page;
    private int sizePerPage;
    private String sortDirection = Sort.Direction.DESC.toString();
    private String sortField = "identifier";
    private int totalPage;
    private long totalRecords;
    private String search;
}
