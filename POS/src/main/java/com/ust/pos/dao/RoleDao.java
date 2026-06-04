package com.ust.pos.dao;

import com.ust.pos.model.Role;

import java.util.List;

public interface RoleDao {
    Role findByIdentifier(String identifier);

    Role save(Role role);

    Role update(Role role);

    void deleteByIdentifier(String identifier);

    List<Role> findByStatusTrue();
}