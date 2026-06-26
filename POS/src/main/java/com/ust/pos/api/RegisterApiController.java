package com.ust.pos.api;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/registeruser")
public class RegisterApiController extends BaseController{
    private UserService userService;

    public RegisterApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDto userDto)
    {
        UserDto response = userService.save(userDto);

        if(!response.isSuccess())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", response.getMessage()));
        }
        return ResponseEntity.ok(Map.of("message", "Registration Successful!"));
    }
}
