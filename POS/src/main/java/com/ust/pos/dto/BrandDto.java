package com.ust.pos.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandDto extends CommonDto {
    @Column(length = 500)
    private String description;

}
