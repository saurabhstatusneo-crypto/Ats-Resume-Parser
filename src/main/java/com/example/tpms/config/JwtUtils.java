package com.example.tpms.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private String jwtSecret = "yourSecretKeyForEncryptionMustBeVeryLongAndStrongyourapikeyhasstobeeeeeeeemorreeeeeeseeeeecccccrrrratteee";
    private int jwtExpirationMs = 86400000; // 24 hours

    public String generateToken(String username, String department) {
        return Jwts.builder()
                .setSubject(username)
                .claim("dept", department) // Store department in token
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) { return false; }
    }
}
