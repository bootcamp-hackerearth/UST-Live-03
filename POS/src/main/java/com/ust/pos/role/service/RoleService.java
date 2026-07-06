package com.ust.pos.role.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RoleService {

    RoleDto save(RoleDto userDto);

    RoleDto update(RoleDto userDto);

    void delete(String username);

    PaginationResponseDto<RoleDto> findAll(Pageable pageable);

    RoleDto findByIdentifier(String identifier);

    RoleDto toggleStatus(String identifier, boolean status);

    PaginationResponseDto<RoleDto> findAll(Specification<Role> example, Pageable pageable);
}