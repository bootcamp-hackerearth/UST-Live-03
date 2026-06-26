package com.ust.pos.api.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SecurityControllerApi {
    private final UserService userService;

    public SecurityControllerApi(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserDto addPost(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }
}
