package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class ApiUserController extends BaseController {

    public static final String MESSAGE = "message";
    public static final String ROLES = "roles";
    public static final String USER_USER = "user/user";

    @Autowired
    private UserService userService;

    @PostMapping("/list")
    public List<UserDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return userService.findAll(pageable);
    }

    @PostMapping("/register")
    public UserDto add(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    public UserDto update(@RequestParam String username) {
        return userService.findByUserName(username);
    }

    @PostMapping("/update")
    public UserDto updatePost(@RequestBody UserDto userDto, @RequestParam String oldUsername) {
        return userService.update(oldUsername, userDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String username) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                String loggedInUser = authentication.getName();

                if (loggedInUser != null) {
                    userService.delete(username);

                    if (loggedInUser.equals(username)) {
                        SecurityContextHolder.clearContext();
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
