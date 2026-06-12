package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto findByUserName(String username);

    WsDto<UserDto> findAll(Pageable pageable);

    UserDto update(UserDto userDto);

    UserDto toggleStatus(String username);

    void delete(String username);
}
