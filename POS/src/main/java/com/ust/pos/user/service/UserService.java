package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import com.ust.pos.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(String username);

    List<UserDto> findAll();

    Page<UserDto> findAll(Example<User> example, Pageable pageable);

    WsDto<UserDto> findAll(Pageable pageable);
}
