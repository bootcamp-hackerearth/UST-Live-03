package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto roleDto);

    List<RoleDto> findAll(Pageable pageable);

    List<RoleDto> findAllActive();

    RoleDto findByIdentifier(String identifier);

    RoleDto update(RoleDto roleDto);

    RoleDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
