package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.user.service.UserService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasAnyAuthority('Admin')")
public class UserApiController extends BaseController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/list")
    public WsDto<UserDto> home(@RequestBody PaginationDto paginationDto) throws Exception {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        Example<User> example = buildSearchProbe(User.class, paginationDto.getSearch(), "password");
        Page<UserDto> pageResult = userService.findAll(example, pageable);
        WsDto<UserDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }
    @PostMapping("/listuser")
    public UserDto list(@RequestParam String username)
    {
        return userService.findByUserName(username);
    }

    @GetMapping("/get")
    public UserDto update(@RequestParam String username, @ModelAttribute UserDto userDto) {
        return userService.findByUserName(username);
    }

    @GetMapping("/getcurrentuser")
    @PreAuthorize("isAuthenticated()")
    public UserDto update(@RequestParam String username)
    {
        return userService.findByUserName(username);
    }
    @PostMapping("/add")
    public UserDto addPost(@RequestBody UserDto userDto)
    {
        return userService.save(userDto);
    }

    @PutMapping("/update")
    public UserDto updatePost(@RequestBody UserDto user) {
        return userService.update(user);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String username) {
        try {
            userService.delete(username);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
