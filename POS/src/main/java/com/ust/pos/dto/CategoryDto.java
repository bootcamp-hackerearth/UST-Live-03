package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryDto extends CommonDto{
    private String superCategory;
    private Boolean status=true;
}
