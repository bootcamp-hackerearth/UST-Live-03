package com.ust.pos.api;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class SecurityApiController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

}