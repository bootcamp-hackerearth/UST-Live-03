package com.ust.pos.dao.impl;

import com.ust.pos.dao.UserDao;
import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserDaoImpl(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean save(UserDto userDto) {
        String encodePass = passwordEncoder.encode(userDto.getPassword());
        String sqlQ = "insert into user set username = ?, name = ?, phone_no = ?, password = ?";
        jdbcTemplate.update(sqlQ, userDto.getUsername(), userDto.getName(), userDto.getPhoneNo(), encodePass);
        return false;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "select * from user where username = ?";
        List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), username);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public List<User> getData() {
        String sql = "select * from user";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void deleteUserJdbc(String username) {
        String sql = "delete from user where username = ?";
        jdbcTemplate.update(sql, username);
    }

    @Override
    public User findById(Long id) {
        String sql = "select * from user where id = ?";
        List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), id);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User updateUserJdbc(User user) {
        String sql = "update user set name = ?, phone_no = ? where username = ?";
        jdbcTemplate.update(sql, user.getName(), user.getPhoneNo(), user.getUsername());
        return user;
    }
}