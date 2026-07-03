package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto roleDto);

    WsDto<RoleDto> findAll(Pageable pageable);

    WsDto<RoleDto> findAll(Specification<Role> example, Pageable pageable);

    List<RoleDto> findAllActive();

    RoleDto findByIdentifier(String identifier);

    RoleDto update(RoleDto roleDto);

    RoleDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
