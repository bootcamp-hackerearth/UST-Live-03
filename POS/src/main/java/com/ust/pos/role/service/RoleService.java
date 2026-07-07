package com.ust.pos.role.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RoleService {

    RoleDto save(RoleDto roleDto);

    RoleDto update(RoleDto roleDto);

    RoleDto delete(String identifier);

    PaginatedResponseDto<RoleDto> findAll(Pageable pageable);

    RoleDto findByIdentifier(String identifier);

    List<RoleDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<RoleDto> findAll(Specification<Role> example, Pageable pageable);
}