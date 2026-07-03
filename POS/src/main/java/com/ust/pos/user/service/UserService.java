package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto findByUserName(String username);

    WsDto<UserDto> findAll(Pageable pageable);

    WsDto<UserDto> findAll(Specification<User> example, Pageable pageable);

    UserDto update(UserDto userDto);

    UserDto toggleStatus(String username);

    boolean delete(String username);
}
