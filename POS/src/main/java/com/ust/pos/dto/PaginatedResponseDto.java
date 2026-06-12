package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedResponseDto<T> extends PaginationDto {
    private List<T> items;
}
