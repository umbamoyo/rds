package com.example.rdsapi.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class CommonUtil {

    @Bean
    public Random random(){
        return new Random();
    }
}
