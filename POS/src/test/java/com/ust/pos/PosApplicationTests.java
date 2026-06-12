package com.ust.pos;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PosApplicationTests {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        assertNotNull(modelMapper);
        assertNotNull(jdbcTemplate);
        assertNotNull(dataSource);
        assertNotNull(passwordEncoder);
    }

    @Test
    void testModelMapperBean() {
        assertNotNull(modelMapper);
    }

    @Test
    void testJdbcTemplateBean() {
        assertNotNull(jdbcTemplate);
        assertEquals(dataSource, jdbcTemplate.getDataSource());
    }

    @Test
    void testDataSourceBean() throws Exception {
        assertNotNull(dataSource);
        assertNotNull(dataSource.getConnection());
    }

    @Test
    void testPasswordEncoderBean() {
        String password = "admin123";
        String encoded = passwordEncoder.encode(password);
        assertNotNull(encoded);
        assertTrue(passwordEncoder.matches(password, encoded));
    }
}