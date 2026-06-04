package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(String username);

    List<UserDto> findAll();
}
