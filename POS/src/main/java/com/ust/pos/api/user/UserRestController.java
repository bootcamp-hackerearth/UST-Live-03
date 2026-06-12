package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserRestController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public WsDto<UserDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return userService.findAll(pageable);
    }

    @PostMapping("/get")
    public UserDto update(@RequestBody String username) {
        return userService.findByUserName(username);
    }

    @PostMapping("/update")
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody String username) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                String loggedInUser = authentication.getName();
                userService.delete(username);
                if (loggedInUser.equals(username)) {
                    SecurityContextHolder.clearContext();
                    return true;
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/add")
    public UserDto registerUser(@RequestBody UserDto userDto) {
        if (userService.findByUserName(userDto.getUsername()) == null) {
            return userService.save(userDto);
        }
        return userDto;
    }
}
