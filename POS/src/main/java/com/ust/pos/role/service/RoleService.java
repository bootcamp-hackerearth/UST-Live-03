package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto userDto);

    RoleDto update(RoleDto userDto);

    void delete(String username);

    Page<RoleDto> findAll(Example<Role> example, Pageable pageable);

    List<RoleDto> findAll();

    WsDto<RoleDto> findAll(Pageable pageable);

    RoleDto findByIdentifier(String identifier);
}
