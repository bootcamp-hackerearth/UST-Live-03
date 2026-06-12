package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto roleDto);

    RoleDto update(RoleDto roleDto);

    void delete(String username);

    WsDto<RoleDto> findAll(Pageable page);

    RoleDto findByIdentifier(String identifier);

    List<RoleDto> findActiveRole();

    RoleDto toggleStatus(String identifier, boolean status);
}