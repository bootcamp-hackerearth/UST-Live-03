package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserControllerApi extends BaseController {

    private final UserService userService;

    public UserControllerApi(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/list")
    public PageDto<UserDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );
        return userService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public UserDto getByUsername(@RequestParam String username) {
        return userService.findByUserName(username);
    }

    @PostMapping("/add")
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PutMapping("/update")
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String username) {
        try {
            userService.delete(username);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}