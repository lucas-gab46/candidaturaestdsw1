package br.com.lucas.candidaturaestagio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/",
                    "/login",
                    "/logout",
                    "/candidato/login",
                    "/candidato/register",
                    "/empresa/register",
                    "/vagas",
                    "/profissionais",
                    "/empresas",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/static/**",
                    "/error/**"
                ).permitAll()
                .anyRequest().permitAll()
            )
            .formLogin().disable()
            .httpBasic().disable();
        return http.build();
    }
}
