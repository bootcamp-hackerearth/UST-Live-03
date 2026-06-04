package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto roleDto);

    void delete(String identifier);

    List<RoleDto> findAll();

    RoleDto findByIdentifier(String identifier);

    RoleDto update(RoleDto roleDto);
}
