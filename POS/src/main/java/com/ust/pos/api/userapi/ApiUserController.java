package com.ust.pos.api.userapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.WsDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.user.service.UserService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class ApiUserController extends BaseController {

    private final UserService userService;

    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public WsDto<UserDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<User> example = buildGlobalSearchSpec(User.class, paginationDto.getKeyword());
            if (example != null) {
                return userService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return userService.findAll(pageable);
    }

    @PostMapping("/register")
    public UserDto addPost(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UserDto update(@RequestParam String username) {
        return userService.findByUserName(username);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public boolean delete(@RequestParam String username) {
        try {
            userService.delete(username);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UserDto toggle(@RequestParam String username) {
        return userService.toggleStatus(username);
    }

    @GetMapping("/findByStatus")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<UserDto> findByStatus() {
        return userService.findIfTrue();
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','INVENTORY_MANAGER','MANAGER','AUDITOR','CASHIER')")
    public UserDto getProfile(Authentication authentication) {
        return userService.getUserDetails(authentication.getName());
    }
}