package com.AAZl3l3.NettyWSServe.utils;

import com.AAZl3l4.common.pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;

public class JwtUtil {
    private static String jwtSecret = "niyongyuancaibudao";
    private static Long jwtExpiration =60000L;

    private static JacksonObjectMapper objectMapper = new JacksonObjectMapper();

    public static String create(User user) {
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

    public static User analysis(String JWT) {
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