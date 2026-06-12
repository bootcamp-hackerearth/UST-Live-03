package com.ust.pos.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WsDto <T> extends PaginationDto{
    List<T> dtoList;
}