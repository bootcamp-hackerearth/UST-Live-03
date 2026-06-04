package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto roleDto);

    RoleDto update(RoleDto roleDto);

    void delete(String username);

    List<RoleDto> findAll();

    List<RoleDto> findAll(Pageable pageable);

    RoleDto findByIdentifier(String identifier);
}
