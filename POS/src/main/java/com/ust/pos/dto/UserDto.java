package com.ust.pos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto extends CommonDto {

    private String name;
    private String username;
    private String phoneNo;
    private List<String> roles;
    private String password;
    private String token;
    public UserDto(String token) {

        this.token = token;

    }
}