package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RackDto extends CommonDto {
    private List<String> shelves;
}
