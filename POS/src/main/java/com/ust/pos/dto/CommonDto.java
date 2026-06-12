package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonDto {
    private Long id;
    private String identifier;
    private String message;
    private boolean success = true;
}
