package com.ust.pos;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.ust.pos.web.controller", "com.ust.pos"})
public class PosApplication {

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