package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class WsDto<T> extends PaginationDto{
    List<T> dtoList;
}