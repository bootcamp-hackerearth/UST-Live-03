package com.ust.pos.dao.impl;

import com.ust.pos.dao.UserDao;
import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDaoImpl implements UserDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean save(UserDto userDto) {
        String encodePass = passwordEncoder.encode(userDto.getPassword());
        String sqlQ = "insert into user set username = ? , name = ? , phone_no = ? , password = ?";
        jdbcTemplate.update(sqlQ, userDto.getUsername(), userDto.getName(), userDto.getPhoneNo(), encodePass);
        return false;
    }

    @Override
    public User findByUsername(String username) {
        String s1 = "select * from user where username = ?";
        List<User> user = jdbcTemplate.query(s1, new Object[]{username}, new BeanPropertyRowMapper<>(User.class));
        return user.isEmpty() ? null : user.get(0);
    }

    @Override
    public List<User> getData() {
        String s1 = "select * from user";
        return jdbcTemplate.query(s1, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void deleteUserJdbc(String username) {
        String s1 = "delete from user where username = ?";
        jdbcTemplate.update(s1, username);
    }

    @Override
    public User findById(Long id) {
        String s1 = "select * from user where id=?";
        List<User> user = jdbcTemplate.query(s1, new Object[]{id}, new BeanPropertyRowMapper<>(User.class));
        return user.get(0);
    }

    @Override
    public User updateUserJdbc(User user) {
        String s1 = "update user set  name = ?, phone_no = ?  where username = ?";
        jdbcTemplate.update(s1, user.getName(),user.getPhoneNo(), user.getUsername());
        return user;
    }
}
