package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface RoleService {
    RoleDto save(RoleDto roleDto);

    RoleDto update(RoleDto roleDto);

    boolean delete(String identifier);

    List<RoleDto> findAll();

    RoleDto findByIdentifier(String identifier);
}