package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RacksDto extends CommonDto {
    private boolean status;
    private String shelfIdentifier;
}
