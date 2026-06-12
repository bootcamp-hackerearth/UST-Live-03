package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(String username);

    WsDto<UserDto> findAll(Pageable pageable);

    UserDto changeToggleStatus(Long id, boolean status);

    List<UserDto> findActiveStatus();
}
