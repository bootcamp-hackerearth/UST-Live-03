package com.ust.pos.user.service;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {
    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(String username);

    WsDto<UserDto> findAll(Pageable pageable);

    WsDto<UserDto> findAll(Specification<User>example, Pageable pageable);

    UserDto changeToggleStatus(Long id, boolean status);

    List<UserDto> findActiveStatus();

}
