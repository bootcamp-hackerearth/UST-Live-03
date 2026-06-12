package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface RoleService {
    RoleDto save(RoleDto roleDto);

    RoleDto update(RoleDto roleDto);

    void delete(String username);

    WsDto<RoleDto> findAll(Pageable pageable);

    RoleDto findByIdentifier(String identifier);
}
