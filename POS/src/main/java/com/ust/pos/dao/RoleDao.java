package com.ust.pos.dao;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import org.springframework.stereotype.Component;

@Component
public interface RoleDao {

    Role save(RoleDto roleDto);

    Role update(RoleDto roleDto);

    void deleteByIdentifier(String identifier);

    Role findByIdentifier(String identifier);
}