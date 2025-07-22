package neko.authorization.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import neko.authorization.service.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateJwtToken(String username, Set<Role> roles) {
        Algorithm algorithm = Algorithm.HMAC512(secretKey);

        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles.toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    public String getUsernameFromJwtToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

    public boolean isTokenExpired(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
        return decodedJWT.getExpiresAt().before(new Date());
    }

    public boolean validateJwtToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}