package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {

    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(String oldUsername, UserDto userDto);

    void delete(String username);

    WsDto<UserDto> findAll(Pageable pageable);

    WsDto<UserDto> findAll(Specification<User> example, Pageable pageable);

}
