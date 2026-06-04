package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto nodeDto);

    RoleDto update(RoleDto nodeDto);

    void delete(String username);

    List<RoleDto> findAll();

    RoleDto findByIdentifier(String identifier);
}
