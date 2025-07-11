package cn.master.luna.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Created by 11's papa on 2025/7/9
 */
@Component
public class JwtTokenUtil {

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    // 根据物理地址哈希得到，服务每次重启都不一样
    // SecretKey key = Jwts.SIG.HS512.key().build();
    // 使用固定密钥：使用固定的字符串生成 HMAC 密钥，方便在开发和测试时使用
    SecretKey key = Keys.hmacShaKeyFor("G!p8x$2wQ9eR7tY4iU3oP0aS6dF9hJ2kL5vN1bM".getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("hé lô", "tạo claim chơi nè");

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .and()
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
