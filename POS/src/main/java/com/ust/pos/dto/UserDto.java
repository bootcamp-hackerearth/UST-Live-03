package com.ust.pos.dto;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

@OpenAPIDefinition(
        servers = {
                @Server(url = "/", description = "Default Server URL")
        }
)
@ComponentScan({"com.ust.pos.api", "com.ust.pos.web.controller", "com.ust.pos"})
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