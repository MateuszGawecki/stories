package com.company.stories.security;

import com.company.stories.security.filter.CustomAuthorizationFilter;
import com.company.stories.service.LogService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private static final String USER = "user";
    private static final String MODERATOR = "moderator";
    private static final String ADMIN = "admin";

    private static final String[] AUTH_WHITE_LIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v2/api-docs/**",
            "/swagger-resources/**",

            "/api/security/authenticate",
            "/api/security/token/refresh",
            "/api/security/register",
            "/api/security/logout"
    };

    private static final String[] USER_GET_METHOD_WHITE_LIST = {
            "/api/users/**",
            "/api/books/**",
            "/api/images/**"
    };

    private static final String[] USER_ALL_METHODS_WHITE_LIST = {
            "/api/users/friends/**",
            "/api/users/books/**"
    };

    private static final String[] MODERATOR_WHITE_LIST = {
            "/api/books/**",
            "/api/authors/**",
            "/api/genres/**"
    };

    private static final String[] ADMIN_WHITE_LIST = {
            "/api/users/**",
            "/api/logs/**",
            "/api/roles/**"
    };

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://127.0.0.1:3000/",
            "http://localhost:3000/",
            "http://127.0.0.1:8080/",
            "http://192.168.0.67:8080/",
            "http://192.168.0.67:8080",
            "http://localhost:8080/",

            "http://127.0.0.1:8081/"
    );

    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE"
    );

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LogService logService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**");
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(req ->
                req.requestMatchers(AUTH_WHITE_LIST).permitAll()
                        .requestMatchers(GET, "/", "/static/**").permitAll()
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers(GET, USER_GET_METHOD_WHITE_LIST).hasAuthority(USER)
                        .requestMatchers(POST, "/api/images/**").hasAuthority(USER)
                        .requestMatchers(USER_ALL_METHODS_WHITE_LIST).hasAuthority(USER)
                        .requestMatchers(MODERATOR_WHITE_LIST).hasAuthority(MODERATOR)
                        .requestMatchers(ADMIN_WHITE_LIST).hasAuthority(ADMIN)
                        .anyRequest().denyAll()
        );
//        //https
//        http.requiresChannel().anyRequest().requiresSecure();
//
//        //front
//        http.headers().frameOptions().sameOrigin();
//        http.headers().contentSecurityPolicy(
//                "script-src 'self'; "
//                        + "style-src 'self'; "
//                        + "img-src 'self' data: blob: ; "
//                        + "object-src 'none';"
//                        + " default-src 'self'; "
//                        + "form-action 'self'; "
//                        + "base-uri 'none'; "
////                        + "upgrade-insecure-requests;"
//        );
//
//        //nosniff
//        http.headers().contentTypeOptions();

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
