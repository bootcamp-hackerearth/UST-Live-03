package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserApiController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/list")
    public WsDto<UserDto> list(@RequestBody PaginationDto paginationDto) {
        if ("identifier".equals(paginationDto.getSortField())) {
            paginationDto.setSortField("username");
        }
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return userService.findAll(pageable);
    }

    @PostMapping("/add")
    public UserDto add(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    public UserDto get(@RequestParam String identifier) {
        return userService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public UserDto update(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            userService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/profile")
    public UserDto getProfile(Authentication authentication) {
        return userService.getUserDetails(authentication.getName());
    }
}