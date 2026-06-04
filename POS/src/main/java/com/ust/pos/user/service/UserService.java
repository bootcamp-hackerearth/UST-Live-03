package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto findByUserName(String username);

    List<UserDto> findAll(Pageable pageable);

    UserDto update(UserDto userDto);

    UserDto toggleStatus(String username);

    void delete(String username);
}
