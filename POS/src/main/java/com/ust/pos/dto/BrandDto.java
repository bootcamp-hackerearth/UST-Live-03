package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BrandDto extends CommonDto {
    private MultipartFile icon;
}
