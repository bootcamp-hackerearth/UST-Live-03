package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationResponseDto<T> extends PaginationDto {
    List<T> dtoList;
}