package com.ust.pos.role.service;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface RoleService {
    RoleDto save(RoleDto roleDto);

    RoleDto update(RoleDto roleDto);

    boolean delete(String identifier);

    PageDto<RoleDto> findAll(Pageable pageable);

    RoleDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<RoleDto> findActiveRoles();

    PageDto<RoleDto> findAll(Specification<Role> spec, Pageable pageable, String keyword);
}
