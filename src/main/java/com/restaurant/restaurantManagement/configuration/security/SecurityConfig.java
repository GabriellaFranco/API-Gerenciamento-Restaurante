package com.restaurant.restaurantManagement.configuration.security;

import com.restaurant.restaurantManagement.exception.CustomBasicAuthEntryPoint;
import com.restaurant.restaurantManagement.filters.PerformanceLoggingFilter;
import com.restaurant.restaurantManagement.filters.RequestLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final RequestLoggingFilter requestLoggingFilter;
    private final PerformanceLoggingFilter performanceLoggingFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/users", "/users/**").hasRole("OWNER")
                                .requestMatchers(HttpMethod.POST, "/users", "/products", "/inventories",
                                        "/inventories/**").hasRole("OWNER")
                                .requestMatchers(HttpMethod.GET, "/products", "/products/**", "/inventories",
                                        "/inventories/**").hasAnyRole("EMPLOYEE", "OWNER")
                                .requestMatchers(HttpMethod.PUT, "/products", "/products/**").hasAnyRole("EMPLOYEE", "OWNER")
                                .requestMatchers(HttpMethod.DELETE, "/users", "/products").hasRole("OWNER")
                                .requestMatchers("/login", "/system-error-reason").permitAll()
                        )
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(performanceLoggingFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin(Customizer.withDefaults());
        http.httpBasic(sbc -> sbc.authenticationEntryPoint(new CustomBasicAuthEntryPoint()));
        return http.build();
    }
}
