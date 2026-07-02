package com.ust.pos.user.service;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
    UserDto findByUserName(String username);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    boolean delete(String username);

    PageDto<UserDto> findAll(Pageable pageable);

    PageDto<UserDto> findAll(Specification<User> spec, Pageable pageable, String keyword);
}
