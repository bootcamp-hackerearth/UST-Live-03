package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {

    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(String identifier);

    PaginationResponseDto<UserDto> findAll(Pageable pageable);

    PaginationResponseDto<UserDto> findAll(Specification<User> example, Pageable pageable);
}