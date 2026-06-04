package com.ust.pos.dao.impl;

import com.ust.pos.dao.RoleDao;
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
    public Role save(Role role) {
        String sql = "INSERT INTO ROLE (identifier, description, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(
                sql,
                role.getIdentifier(),
                role.getDescription(),
                role.isStatus()
        );
        return findByIdentifier(role.getIdentifier());
    }

    @Override
    public Role update(Role role) {
        String sql = "UPDATE ROLE SET description = ? WHERE identifier = ?";
        jdbcTemplate.update(
                sql,
                role.getDescription(),
                role.getIdentifier()
        );
        return findByIdentifier(role.getIdentifier());
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        String sql = "DELETE FROM ROLE WHERE identifier = ?";
        jdbcTemplate.update(sql, identifier);
    }

    @Override
    public List<Role> findByStatusTrue() {
        String sql = "SELECT * FROM ROLE WHERE status = true";
        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(Role.class)
        );
    }
}