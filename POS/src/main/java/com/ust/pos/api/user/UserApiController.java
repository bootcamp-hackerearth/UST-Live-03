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

    @PostMapping("/register")
    public UserDto add(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public WsDto<UserDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage()
                , paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<User> example = buildGlobalSearchSpec(User.class, paginationDto.getKeyword());
            if (example != null) {
                return userService.findAll(example, pageable);
            }
        }
        return userService.findAll(pageable);
    }

    @GetMapping("/get")
    public UserDto getByIdentifier(@RequestParam String identifier) {
        return userService.findByUserName(identifier);
    }

    @PutMapping("/update")
    public UserDto update(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            userService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDto toggleStatus(@RequestParam String identifier) {
        return userService.toggleStatus(identifier);
    }
}
