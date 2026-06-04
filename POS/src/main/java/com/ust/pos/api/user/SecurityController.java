package com.ust.pos.api.user;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("securityApiController")
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    @PostMapping("/register")
    public UserDto register(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/roles")
    public List<RoleDto> roles() {
        return roleService.findAll(Pageable.unpaged());
    }
}