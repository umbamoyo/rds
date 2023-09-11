package com.example.rdsapi.config;

import com.example.rdsapi.security.filter.CustomAccessDeniedHandler;
import com.example.rdsapi.security.filter.JwtAuthenticationProvider;
import com.example.rdsapi.security.filter.JwtCheckExceptionHandler;
import com.example.rdsapi.security.filter.JwtCheckFilter;
import com.example.rdsapi.security.filter.JwtLoginFilter;
import com.example.rdsapi.util.JwtUtil;
import com.example.rdsapi.service.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final UserAccountService userAccountService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager, userAccountService, jwtUtil);
        JwtCheckFilter jwtCheckFilter = new JwtCheckFilter(authenticationManager, jwtUtil);
        JwtCheckExceptionHandler jwtCheckExceptionHandler = new JwtCheckExceptionHandler(authenticationManager, objectMapper);


        return http
                .cors().and()
                .authorizeRequests(auth -> auth
                        .mvcMatchers("/api/v1/**").permitAll()
                )
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(jwtCheckFilter, BasicAuthenticationFilter.class)
                .addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtCheckExceptionHandler, JwtCheckFilter.class)
                .exceptionHandling(config -> config.accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper)))
                .authenticationProvider(new JwtAuthenticationProvider(jwtUtil))
                .csrf().disable()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService){
        return userAccountService::getUserAccountById;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



}

