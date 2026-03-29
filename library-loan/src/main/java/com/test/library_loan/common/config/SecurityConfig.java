package com.test.library_loan.common.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // REST API
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/book/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/book/**").hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")
                        .requestMatchers(HttpMethod.PUT, "/api/book/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/book/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/member/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/member/**").hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")
                        .requestMatchers(HttpMethod.PUT, "/api/member/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/member/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/loan/borrow").hasRole("MEMBER")
                        .requestMatchers(HttpMethod.PUT, "/api/loan/return").hasRole("MEMBER")
                        .requestMatchers(HttpMethod.GET, "/api/loan/**").hasAnyRole("LIBRARIAN", "MEMBER")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setContentType("application/json");
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.getWriter().write("""
                        {
                          "error": "Unauthorized",
                          "message": "Authentication required"
                        }
                    """);
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails librarian = User.builder()
                .username("librarian")
                .password(encoder.encode("librarian123"))
                .roles("LIBRARIAN")
                .build();

        UserDetails member = User.builder()
                .username("member")
                .password(encoder.encode("member123"))
                .roles("MEMBER")
                .build();

        return new InMemoryUserDetailsManager(admin, librarian, member);
    }
}
