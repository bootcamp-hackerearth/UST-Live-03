package com.ust.pos.dao.impl;

import com.ust.pos.dao.RoleDao;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleDaoImpl implements RoleDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Role findByIdentifier(String identifier) {
        String sql = "SELECT * FROM ROLE WHERE identifier = ?";
        List<Role> roleList = jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(Role.class),
                identifier);
        return roleList.isEmpty() ? null : roleList.get(0);
    }

    @Override
    public Role save(RoleDto roleDto) {
        String sql = "INSERT INTO ROLE (identifier, description,status) VALUES (?, ?,?)";
        jdbcTemplate.update(
                sql,
                roleDto.getIdentifier(),
                roleDto.getDescription(),
                roleDto.getStatus());
        return findByIdentifier(roleDto.getIdentifier());
    }

    @Override
    public Role update(RoleDto roleDto) {
        String sql = "UPDATE ROLE SET description=?,status = ? WHERE identifier = ?";
        jdbcTemplate.update(
                sql,
                roleDto.getDescription(),
                roleDto.getStatus(),
                roleDto.getIdentifier());
        return findByIdentifier(roleDto.getIdentifier());
    }

}