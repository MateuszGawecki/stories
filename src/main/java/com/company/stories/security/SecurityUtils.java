package com.company.stories.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.stream.Collectors;

public abstract class SecurityUtils {
    private static final long ACCESS_TOKEN_VALID_TIME = 15 * 60 * 1000; // 15 minutes in milliseconds
    private static final long REFRESH_TOKEN_VALID_TIME = 2 * 60 * 60 * 1000; // 2 hours in milliseconds
    private static final String ROLE_ID = "roles";
    private static final String SECRET_STRING = "secret";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_STRING.getBytes());

    public static String createAccessToken(UserDetails user, String issuer){
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALID_TIME))
                .withIssuer(issuer)
                .withClaim(ROLE_ID, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public static String createRefreshToken(UserDetails user, String issuer) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public static DecodedJWT verifyToken(String token){
        JWTVerifier verifier = JWT.require(algorithm).build();

        return verifier.verify(token);
    }
}
