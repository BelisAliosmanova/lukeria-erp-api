package com.example.ludogoriesoft.lukeriaerpapi.config;


import com.example.ludogoriesoft.lukeriaerpapi.handler.JwtAuthenticationEntryPoint;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.example.ludogoriesoft.lukeriaerpapi.enums.Role.*;
import static org.springframework.http.HttpMethod.GET;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper))
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/v1/images/uploadImageForPackage",
                        "/api/v1/carton/**",
                        "/api/v1/client/**",
                        "/api/v1/plate/**",
                        "/test/**",
                        "/api/v1/invoice/**",
                        "/api/v1/images/**",
                        "/api/v1/invoiceOrderProduct/**",
                        "/api/v1/material-order/**",
                        "/api/v1/monthlyOrder/**",
                        "/api/v1/monthlyOrderProduct/**",
                        "/api/v1/order/**",
                        "/api/v1/orderProduct/**",
                        "/api/v1/user/**",
                        "/api/v1/auth/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"
                )
                .permitAll()


                .requestMatchers(GET,"/api/v1/package/**").hasAnyRole(ADMIN.name(), PRODUCTION_MANAGER.name())
//                .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
//                .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
//                .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
//                .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
//                .requestMatchers(POST, "/api/v1/categories/**").hasAnyAuthority(ADMIN_CREATE.name())
//                .requestMatchers(PUT, "/api/v1/categories/**").hasAnyAuthority(ADMIN_UPDATE.name())
//                .requestMatchers(DELETE, "/api/v1/categories/**").hasAnyAuthority(ADMIN_DELETE.name())
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());

        return http.build();
    }
}
