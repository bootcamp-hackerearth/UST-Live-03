package com.ust.pos.user.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {

    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto delete(String username);

    PaginatedResponseDto<UserDto> findAll(Pageable pageable);

    List<UserDto> findAllActive();

    void changeStatus(String username, boolean status);

    PaginatedResponseDto<UserDto> findAll(Specification<User> example, Pageable pageable);
}
