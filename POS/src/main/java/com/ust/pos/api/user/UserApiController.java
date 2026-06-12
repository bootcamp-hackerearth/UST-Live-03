package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserApiController extends BaseController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserDto add(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PostMapping("/list")
    public WsDto<UserDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage()
                , paginationDto.getSortDirection(), paginationDto.getSortField());
        return userService.findAll(pageable);
    }

    @GetMapping("/get")
    public UserDto getByIdentifier(@RequestParam String identifier) {
        return userService.findByUserName(identifier);
    }

    @PostMapping("/update")
    public UserDto update(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String username) {
        try {
            userService.delete(username);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    public UserDto toggleStatus(@RequestParam String identifier) {
        return userService.toggleStatus(identifier);
    }
}
