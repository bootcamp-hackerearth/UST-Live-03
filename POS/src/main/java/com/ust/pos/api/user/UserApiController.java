package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserApiController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/list")
    public PaginationResponseDto<UserDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return userService.findAll(pageable);
    }

    @PostMapping("/add")
    public UserDto add(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    public UserDto update(@RequestParam String username) {
        return userService.findByUserName(username);
    }

    @PostMapping("/update")
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            userService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/register")
    public UserDto addPost(@RequestBody UserDto userDto){
        return userService.save(userDto);
    }
}