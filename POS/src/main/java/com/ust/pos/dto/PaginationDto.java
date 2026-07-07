package com.ust.pos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaginationDto {
    private int page;
    private int sizePerPage = 10;
    private String sortDirection = Sort.Direction.ASC.toString();
    private String sortField = "id";
    private int totalPages;
    private long totalRecords;
    private String keyword;
}