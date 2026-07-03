package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto roleDto);

    RoleDto update(RoleDto roleDto);

    void delete(String identifier);

    WsDto<RoleDto> findAll(Pageable page);

    RoleDto findByIdentifier(String identifier);

    List<RoleDto> findActiveRole();

    RoleDto toggleStatus(String identifier, boolean status);

    WsDto<RoleDto> findAll(Specification<Role> example, Pageable pageable);
}