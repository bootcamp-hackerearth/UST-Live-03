package com.ust.pos.user.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto delete(String username);

    PaginatedResponseDto<UserDto> findAll(Pageable pageable);

    List<UserDto> findAllActive();

    void changeStatus(String username, boolean status);
}
