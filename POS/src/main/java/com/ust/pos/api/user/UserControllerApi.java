package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserControllerApi extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/list")
    public List<UserDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return userService.findAll(pageable);
    }

    @PostMapping("/add")
    public UserDto addPost(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    public UserDto getUser(@RequestParam String username) {
        return userService.findByUserName(username);
    }

    @PostMapping("/update")
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @PostMapping("/delete")
    public UserDto delete(@RequestBody UserDto userDto) {
        UserDto response = new UserDto();
        try {
            userService.delete(userDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("User deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }
}