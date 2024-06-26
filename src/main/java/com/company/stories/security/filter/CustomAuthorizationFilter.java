package com.company.stories.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.entity.Log;
import com.company.stories.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private static final String ROLE_ID = "roles";
    private static final String BEARER= "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/api/security/authenticate") ||
                request.getServletPath().equals("/api/security/token/refresh") ||
                request.getServletPath().equals("/api/security/register") ||
                request.getServletPath().equals("/api/security/logout") ||
                request.getServletPath().equals("/")
        ){
            log.info("Request to login, refresh token, register, logout or root path");
            filterChain.doFilter(request,response);
        }else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(authorizationHeader != null && authorizationHeader.startsWith(BEARER)){
                try {
                    String token = authorizationHeader.substring(BEARER.length());
                    DecodedJWT decodedJWT = SecurityUtils.verifyToken(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim(ROLE_ID).asArray(String.class);

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }catch (Exception ex){
                    log.error("Error in Authorization Filter " + request.getServletPath());
                    response.setHeader("error", ex.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    Map<String,String> error = new HashMap<>();
                    error.put("error_message", ex.getMessage());

                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }else {
                log.error("Error in Authorization Filter 2 " + request.getServletPath());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(APPLICATION_JSON_VALUE);
            }

            filterChain.doFilter(request, response);
        }
    }
}
