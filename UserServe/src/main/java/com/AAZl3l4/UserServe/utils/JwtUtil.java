package com.AAZl3l4.UserServe.utils;

import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.JacksonObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class JwtUtil {
    @Value("${Jwt.secret}")
    private String jwtSecret;
    @Value("${Jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private JacksonObjectMapper objectMapper;

    public String create(User user) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("user", objectMapper.writeValueAsString(user));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize user object", e);
        }
        return Jwts.builder().setClaims(map)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 60000))
                .compact();
    }

    public User analysis(String JWT) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(JWT)
                .getBody();
        try {
            return objectMapper.readValue(claims.get("user").toString(), User.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize user object", e);
        }
    }

}