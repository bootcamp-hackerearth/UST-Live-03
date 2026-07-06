package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService {

    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto delete(String username);

    WsDto<UserDto> findAll(Pageable pageable);

    List<UserDto> findIfTrue();

    UserDto toggleStatus(String identifier);

    UserDto getUserDetails(String username);

    WsDto<UserDto> findAll(Specification<User> example, Pageable pageable);
}