package com.ust.pos;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
@ComponentScan({"com.ust.pos.web.controller", "com.ust.pos"})
public class PosApplication {
    @Autowired
    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(PosApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.getConfiguration().setCollectionsMergeEnabled(false);
        return mapper;
    }
}