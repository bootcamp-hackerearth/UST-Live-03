package com.ust.pos.api.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("userApiController")
@RequestMapping("/api/user")
public class UserController extends BaseController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/list")
    public WsDto<UserDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return userService.findAll(pageable);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getByUsername(@PathVariable String username) {
        UserDto response = userService.findByUserName(username);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<UserDto> update(@PathVariable String username, @RequestBody UserDto userDto) {
        userDto.setUsername(username);
        UserDto response = userService.update(userDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<Boolean> delete(@PathVariable String username) {
        try {
            userService.delete(username);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}