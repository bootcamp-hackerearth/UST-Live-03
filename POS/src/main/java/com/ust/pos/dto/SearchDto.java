package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDto extends PaginationDto {
    private String keyword;
}