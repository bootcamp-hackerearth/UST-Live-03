package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.user.service.UserService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserApiController extends BaseController {
    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public WsDto<UserDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<User> example = buildGlobalSearchSpec(User.class, paginationDto.getKeyword());
            if (example != null) {
                return userService.findAll(example, pageable);
            }
        }
        return userService.findAll(pageable);
    }

    @PostMapping("/add")
    public UserDto addPost(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PostMapping("/register")
    public UserDto add(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public UserDto update(@RequestParam String identifier) {
        return userService.findByUserName(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public boolean delete(@RequestBody UserDto userDto) {
        try {
            userService.delete(userDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}