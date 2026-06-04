package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto userDto);

    RoleDto update(RoleDto roleDto);

    void delete(String username);

    List<RoleDto> findAll(Pageable pageable);

    RoleDto findByIdentifier(String identifier);

    RoleDto changeToggleStatus(String identifier, boolean status);

    List<RoleDto> findActiveStatus();
}
