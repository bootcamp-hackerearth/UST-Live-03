package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.user.service.UserService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserControllerApi extends BaseController {

    private final UserService userService;

    @PostMapping("/list")
    public PaginatedResponseDto<UserDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<User> example = buildGlobalSearchSpec(User.class, paginationDto.getKeyword());
            if (example != null) {
                return userService.findAll(example, pageable);
            }
        }
        return userService.findAll(pageable);
    }

    @PostMapping("/register")
    public UserDto add(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/get")
    public UserDto get(@RequestParam String username) {
        return userService.findByUserName(username);
    }

    @PutMapping("/update")
    public UserDto updatePost(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/delete")
    public UserDto delete(@RequestBody UserDto userDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                String loggedInUser = authentication.getName();
                if (loggedInUser != null) {
                    UserDto result = userService.delete(userDto.getUsername());
                    if (loggedInUser.equals(userDto.getUsername())) {
                        SecurityContextHolder.clearContext();
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            UserDto errorDto = new UserDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
        UserDto errorDto = new UserDto();
        errorDto.setSuccess(false);
        errorDto.setMessage("Authentication context is missing");
        return errorDto;
    }

    @GetMapping("/me")
    public UserDto currentUser(Authentication authentication) {
        return userService.findByUserName(authentication.getName());
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody UserDto userDto) {
        try {
            userService.changeStatus(userDto.getIdentifier(), userDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}