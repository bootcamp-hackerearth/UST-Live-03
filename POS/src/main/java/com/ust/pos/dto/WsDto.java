package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WsDto<T> extends PaginationDto {
    private List<T> content;
}
