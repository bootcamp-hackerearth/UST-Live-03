package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ShelfDto extends CommonDto{
    private List<String> racks = new ArrayList<>();
}
