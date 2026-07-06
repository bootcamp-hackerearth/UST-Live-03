package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class UserApiController extends BaseController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserDto addPost(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    public UserDto update(@RequestParam String username) {
        return userService.findByUserName(username);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'STOCK_MANAGER', 'ACCOUNTANT', 'CASHIER')")
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

    @PostMapping("/toggle-status")
    public UserDto toggle(@RequestParam String username) {
        return userService.toggleStatus(username);
    }

    @GetMapping("/findByStatus")
    public List<UserDto> findByStatus() {
        return userService.findIfTrue();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'STOCK_MANAGER', 'ACCOUNTANT', 'CASHIER')")
    @GetMapping("/api/profile")
    public UserDto getProfile(Authentication authentication) {
        return userService.getUserDetails(authentication.getName());
    }

    @PostMapping("/list")
    public WsDto<UserDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<User> example = buildGlobalSearchSpec(User.class, paginationDto.getKeyword());
            if (example != null) {
                return userService.findAll(example, pageable);
            }
        }

        return userService.findAll(pageable);
    }
}