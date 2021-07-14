package com.microservices.customer.config;

import com.microservices.customer.service.CustomerDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private String jwtSecret = "secretKey";
    private Integer jwtExpirationMS = 3600000;

    public String generateJwtToken(Authentication authentication){
        CustomerDetailsImpl userPrincipal = (CustomerDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMS))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token){
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(authToken);

            return true;
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public String generateJwtTokenUser(Authentication authentication)  throws Exception {
        CustomerDetailsImpl userPrincipal = (CustomerDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                // .setPayload(userPrincipal.getAuthorities().stream().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMS))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
