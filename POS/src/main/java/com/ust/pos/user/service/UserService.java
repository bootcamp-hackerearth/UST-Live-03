package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    boolean delete(String username);

    List<UserDto> findAll(Pageable pageable);
}