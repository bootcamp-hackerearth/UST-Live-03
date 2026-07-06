package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {

    UserDto findByIdentifier(String identifier);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(String identifier);

    WsDto<UserDto> findAll(Pageable pageable);

    WsDto<UserDto> findAll(Specification<User> example, Pageable pageable);

}
