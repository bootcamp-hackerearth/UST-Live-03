package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(String identifier);

    PaginationResponseDto<UserDto> findAll(Pageable pageable);
}