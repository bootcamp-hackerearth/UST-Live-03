package com.ust.pos.dao.impl;

import com.ust.pos.dao.RoleDao;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Role findByIdentifier(String identifier) {
        String sql = "SELECT * FROM ROLE WHERE identifier = ?";
        List<Role> roleList = jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(Role.class),
                identifier
        );
        return roleList.isEmpty() ? null : roleList.get(0);
    }

    @Override
    public Role save(RoleDto roleDto) {
        String sql = "INSERT INTO ROLE (identifier, description) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                roleDto.getIdentifier());
        return findByIdentifier(roleDto.getIdentifier());
    }

    @Override
    public Role update(RoleDto roleDto) {
        String sql = "UPDATE ROLE SET description = ? WHERE identifier = ?";
        jdbcTemplate.update(sql,
                roleDto.getIdentifier());
        return findByIdentifier(roleDto.getIdentifier());
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        String sql = "DELETE FROM ROLE WHERE identifier = ?";
        jdbcTemplate.update(sql, identifier);
    }

}
