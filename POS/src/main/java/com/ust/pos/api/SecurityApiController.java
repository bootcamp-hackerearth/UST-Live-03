package com.ust.pos.api;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class SecurityApiController {

    private final UserService userService;

    public SecurityApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserDto addPost(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }
}
