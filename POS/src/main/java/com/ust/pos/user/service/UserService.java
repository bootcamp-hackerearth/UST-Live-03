package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService {
    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto delete(String username);

    List<UserDto> findAll();
}