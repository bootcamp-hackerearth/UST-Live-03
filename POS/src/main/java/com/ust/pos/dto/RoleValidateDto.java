package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleValidateDto {
    private String username;
    private List<String> roles;
    private String url;
    private String token;
}
