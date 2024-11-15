package com.grupo1.recursos_tic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.util.Collections;
import java.util.List;

/**
 * Configuración de permisos de rutas.
 */
@Configuration
@EnableWebSecurity
public class  SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AnonymousAuthenticationToken anonymousAuthenticationToken() {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        return new AnonymousAuthenticationToken("ANONYMOUS_USER", "ANONYMOUS_USER", authorities);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {

        http
            //.csrf(Customizer.withDefaults())
            .csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))

            .authorizeHttpRequests(authRequest -> authRequest
                    .requestMatchers(HttpMethod.GET,"/", "/legal", "/privacy", "/login", "/help", "/error",
                            "/css/**", "/js/**", "/img/**", "/webjars/**", "/auth/**", "/favicon.ico").permitAll()

                    //.requestMatchers(HttpMethod.POST,"/cookie", "/logout").permitAll()

                    .requestMatchers(HttpMethod.GET,"/resources/create").authenticated()
                    .requestMatchers(HttpMethod.GET,"/resources/update/{id}").authenticated()
                    .requestMatchers(HttpMethod.GET,"/resources/delete/{id}").authenticated()
                    .requestMatchers(HttpMethod.GET,"/resources/delete").authenticated()
                    .requestMatchers(HttpMethod.GET,"/resources/**").permitAll()

                    // TODO revisar permisos
                    .requestMatchers(HttpMethod.GET,"/api/resources/**").permitAll()

                    .anyRequest().authenticated()
            )

            .formLogin(formLogin -> formLogin
                    .failureUrl("/login?error=true")
                    .permitAll()
            )

            .logout(logout -> logout.permitAll())

            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .accessDeniedPage("/error"));

        return http.build();
    }

}