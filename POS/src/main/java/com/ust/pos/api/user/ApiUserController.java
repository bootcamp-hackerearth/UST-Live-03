package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.user.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
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
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<UserDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<User> example = buildGlobalSearchSpec(User.class, paginationDto.getKeyword());
            if (example != null) {
                return userService.findAll(example, pageable);
            }
        }
        return userService.findAll(pageable);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('Admin')")
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('Admin')")
    public UserDto update(@RequestParam String username) {
        return userService.findByUserName(username);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @Transactional
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestParam String username) {
        try {
            userService.delete(username);
        }
        catch(Exception e){
            return false;
            }
            return true;
        }

    @PostMapping("/toggle")
    public UserDto toggle(@RequestBody UserDto userDto) {
        return userService.changeToggleStatus(userDto.getId(), userDto.isStatus());
    }

    @GetMapping("/profile")
    public UserDto getProfile(Authentication authentication) {

        String username = authentication.getName();

        return userService.findByUserName(username);
    }

    @PostMapping("/findActiveStatus")
    public List<UserDto> findActive() {
        return userService.findActiveStatus();
    }
}
