package com.taskmanager.task_manager_rest_api_spring_boot.config;

import com.taskmanager.task_manager_rest_api_spring_boot.model.CustomUserDetails;
import com.taskmanager.task_manager_rest_api_spring_boot.model.Users;
import com.taskmanager.task_manager_rest_api_spring_boot.repository.UsersRepository;
import com.taskmanager.task_manager_rest_api_spring_boot.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String FRONTEND_LOGIN_URL = "/login";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl(FRONTEND_LOGIN_URL)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true))
                .formLogin(formLogin -> formLogin
                        .successHandler((request, response, auth) -> response
                                .setStatus(HttpServletResponse.SC_OK))
                        .failureHandler((request, response,
                                         exception) -> response.setStatus(
                                HttpServletResponse.SC_BAD_REQUEST))
                        .loginProcessingUrl("/auth/login"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}

