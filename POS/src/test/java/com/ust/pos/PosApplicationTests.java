package com.ust.pos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosApplicationTests {

    @Mock
    private Environment environment;

    @InjectMocks
    private PosApplication posApplication;

    @Test
    @DisplayName("Main method execution smoke test")
    void main_RunsWithoutExceptions() {
        Assertions.assertNotNull(posApplication);
    }

    @Test
    @DisplayName("Verify ModelMapper bean configuration")
    void modelMapper_ReturnsConfiguredInstance() {
        ModelMapper mapper = posApplication.modelMapper();

        Assertions.assertNotNull(mapper);
        Assertions.assertEquals(MatchingStrategies.STRICT, mapper.getConfiguration().getMatchingStrategy());
        Assertions.assertTrue(mapper.getConfiguration().isSkipNullEnabled());
        Assertions.assertFalse(mapper.getConfiguration().isCollectionsMergeEnabled());
    }

    @Test
    @DisplayName("Verify PasswordEncoder bean creation")
    void passwordEncoder_ReturnsBCryptInstance() {
        PasswordEncoder encoder = posApplication.passwordEncoder();

        Assertions.assertNotNull(encoder);
        Assertions.assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    @DisplayName("Verify DataSource bean generation with a configured driver class")
    void getDataSource_WithDriverClass_ReturnsConfiguredDataSource() {
        when(environment.getProperty("spring.datasource.url")).thenReturn("jdbc:mysql://localhost:3306/pos_db");
        when(environment.getProperty("spring.datasource.username")).thenReturn("root");
        when(environment.getProperty("spring.datasource.password")).thenReturn("secret");
        when(environment.getProperty("spring.datasource.driver-class-name")).thenReturn("com.mysql.cj.jdbc.Driver");

        DataSource dataSource = posApplication.getDataSource();

        Assertions.assertNotNull(dataSource);
        Assertions.assertTrue(dataSource instanceof DriverManagerDataSource);

        DriverManagerDataSource dmDataSource = (DriverManagerDataSource) dataSource;
        Assertions.assertEquals("jdbc:mysql://localhost:3306/pos_db", dmDataSource.getUrl());
        Assertions.assertEquals("root", dmDataSource.getUsername());
        Assertions.assertEquals("secret", dmDataSource.getPassword());
    }

    @Test
    @DisplayName("Verify DataSource bean generation when driver class is null")
    void getDataSource_NullDriverClass_ReturnsConfiguredDataSource() {
        when(environment.getProperty("spring.datasource.url")).thenReturn("jdbc:h2:mem:testdb");
        when(environment.getProperty("spring.datasource.username")).thenReturn("sa");
        when(environment.getProperty("spring.datasource.password")).thenReturn("");
        when(environment.getProperty("spring.datasource.driver-class-name")).thenReturn(null);

        DataSource dataSource = posApplication.getDataSource();

        Assertions.assertNotNull(dataSource);
        verify(environment, times(1)).getProperty("spring.datasource.driver-class-name");
    }

    @Test
    @DisplayName("Verify JdbcTemplate bean initialization")
    void jdbcTemplate_ReturnsConfiguredInstance() {
        when(environment.getProperty("spring.datasource.url")).thenReturn("jdbc:h2:mem:testdb");
        when(environment.getProperty("spring.datasource.username")).thenReturn("sa");
        when(environment.getProperty("spring.datasource.password")).thenReturn("");

        JdbcTemplate jdbcTemplate = posApplication.jdbcTemplate();

        Assertions.assertNotNull(jdbcTemplate);
        Assertions.assertNotNull(jdbcTemplate.getDataSource());
    }
}