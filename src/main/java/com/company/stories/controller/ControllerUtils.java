package com.company.stories.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.security.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

public class ControllerUtils {

    public static String getIssuer(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring("Bearer ".length());
        DecodedJWT decodedJWT = SecurityUtils.verifyToken(token);

        return decodedJWT.getSubject();
    }
}
