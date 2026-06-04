package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CategoryDto extends CommonDto {

    private List<String> superCategory;
}
