package com.AAZl3l4.gateway.utils;

import com.AAZl3l4.gateway.pojo.User;
import com.AAZl3l4.gateway.utils.JacksonObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
    @Value("${Jwt.renew}")
    private Long RENEW_THRESHOLD_MINUTES;

    private final JacksonObjectMapper objectMapper = new JacksonObjectMapper();

    public String create(com.AAZl3l4.gateway.pojo.User user) {
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

    /* 滑动窗口续期方法 */
    public String renewIfNeeded(String oldJwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(oldJwt)
                    .getBody();

            Date exp = claims.getExpiration();
            long remaining = exp.getTime() - System.currentTimeMillis();
            long thresholdMs = RENEW_THRESHOLD_MINUTES * 60000L;

            // 剩余时间不足阈值 → 重新签发
            if (remaining < thresholdMs) {
                User user = objectMapper.readValue(claims.get("user").toString(), User.class);
                return create(user);        // 复用原来的 create
            }
            return oldJwt;                  // 还在安全窗口内，原样返回
        } catch (JwtException | IllegalArgumentException | JsonProcessingException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}