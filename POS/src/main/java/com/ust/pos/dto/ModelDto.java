package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModelDto extends CommonDto {
    private boolean status;
    private String name;
}