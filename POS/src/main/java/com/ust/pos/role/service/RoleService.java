package com.ust.pos.role.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RoleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto userDto);

    RoleDto update(RoleDto userDto);

    RoleDto updateStatus(String identifier, boolean status);

    void delete(String username);

    PaginationResponseDto<RoleDto> findAll(Pageable pageable);

    List<RoleDto> findByStatusTrue();

    RoleDto findByIdentifier(String identifier);
}
