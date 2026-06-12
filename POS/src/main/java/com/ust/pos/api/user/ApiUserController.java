package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.UserRepository;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user")
public class ApiUserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/list")
    public WsDto<UserDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return userService.findAll(pageable);
    }

    @PostMapping("/register")
    public UserDto addUser(@RequestBody UserDto userDto) {
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

    @Transactional
    @GetMapping("/delete")
    public boolean delete(@RequestParam String username) {
        try {
            userRepository.deleteByUsername(username);
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
