package com.example.rdsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    // 각 도메인에 수정자, 생성자를 입력할떄 사용되는 메소드
    @Bean
    public AuditorAware<String> auditorAware(){
        return () -> Optional.of("testUser");
    }
}
