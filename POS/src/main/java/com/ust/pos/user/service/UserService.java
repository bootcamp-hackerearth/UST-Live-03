package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDto findByIdentifier(String identifier);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(String identifier);

    WsDto<UserDto> findAll(Pageable pageable);

}
