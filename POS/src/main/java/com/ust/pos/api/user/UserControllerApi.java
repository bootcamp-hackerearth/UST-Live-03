package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserControllerApi extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/list")
    public PaginatedResponseDto<UserDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
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
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @PostMapping("/delete")
    public boolean delete(Model model, @RequestBody UserDto userDto) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                String loggedInUser = authentication.getName();
                if (loggedInUser != null) {

                    userService.delete(userDto.getUsername());

                    if (loggedInUser.equals(userDto.getUsername())) {
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

    @GetMapping("/me")
    public UserDto currentUser(Authentication authentication) {
        return userService.findByUserName(authentication.getName());
    }

    @PostMapping("/toggle")
    public boolean changeStatus(@RequestBody UserDto userDto) {
        try {
            userService.changeStatus(userDto.getIdentifier(), userDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
