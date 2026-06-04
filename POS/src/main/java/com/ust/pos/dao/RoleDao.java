package com.ust.pos.dao;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;

public interface RoleDao {

    Role findByIdentifier(String identifier);

    Role save(RoleDto roleDto);

    Role update(RoleDto roleDto);

    void deleteByIdentifier(String identifier);

}
