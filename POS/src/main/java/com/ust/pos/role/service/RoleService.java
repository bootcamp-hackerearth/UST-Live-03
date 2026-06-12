package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto roleDto);

    RoleDto update(RoleDto roleDto);

    void delete(String username);

    List<RoleDto> findAll();

    RoleDto findByIdentifier(String identifier);

    Page<RoleDto> findAll(Pageable pageable, String search);
}