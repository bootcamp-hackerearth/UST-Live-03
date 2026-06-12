package com.ust.pos.role.service;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface RoleService {

    RoleDto save(RoleDto roleDto);
    RoleDto update(RoleDto roleDto);
    boolean delete(String identifier);
    WsDto<RoleDto> findAll(Pageable pageable);
    RoleDto findByIdentifier(String identifier);
    List<RoleDto> findIfTrue();
    RoleDto toggleStatus(String identifier);

}