package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleValidateDto {
    private List<String> roles;
    private String url;
}
