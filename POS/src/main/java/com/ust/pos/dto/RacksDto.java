package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RacksDto extends CommonDto {
    private Boolean status;
    private List<String> shelves;
}
