package com.ust.pos.role.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RoleDto;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleDto save(RoleDto userDto);

    RoleDto update(RoleDto userDto);

    void delete(String username);

    PaginationResponseDto<RoleDto> findAll(Pageable pageable);

    RoleDto findByIdentifier(String identifier);

    RoleDto toggleStatus(String identifier, boolean status);
}