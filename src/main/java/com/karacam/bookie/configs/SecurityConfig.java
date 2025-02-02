package com.karacam.bookie.configs;

import com.karacam.bookie.core.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
@EnableRedisHttpSession
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationProvider configureAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(this.userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager configureAuthenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(httpRequest -> httpRequest
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/reset-password-init").permitAll()
                        .requestMatchers("/auth/reset-password-submit-new-password").hasAuthority(Role.ANONYMOUS.toString())
                        .requestMatchers("/auth/reset-password-confirm-new-password").hasAuthority(Role.ANONYMOUS.toString())
                        .requestMatchers("/auth/verify-email").hasAuthority(Role.ANONYMOUS.toString())
                        .requestMatchers("/pattern/**").hasAuthority(Role.ADMIN.toString())
                        .anyRequest().hasAuthority(Role.USER.toString()))

                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(httpSecurityRequestCacheConfigurer -> httpSecurityRequestCacheConfigurer.requestCache(new NullRequestCache()))
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER));

        return http.build();
    }

    @Bean
    public PasswordEncoder configurePasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public HttpSessionIdResolver configureSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

    @Bean
    public HttpSessionEventPublisher configureSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}

