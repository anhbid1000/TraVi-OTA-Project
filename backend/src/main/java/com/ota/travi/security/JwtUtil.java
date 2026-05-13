package com.ota.travi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${SECRET_KEY}")
    private String secretKey;

    @Value("${JWT_EXPIRATION}")
    private Long expirationTime;

    // 1. Hàm sinh ra Thẻ từ (Token) khi User đăng nhập thành công
    public String generateToken(String username, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role); // Nhét thêm quyền (Guest, Partner...) vào thẻ

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username) // Tên người dùng sở hữu thẻ
                .setIssuedAt(new Date(System.currentTimeMillis())) // Thời gian phát thẻ
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Thời gian hết hạn
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Đóng dấu mộc
                .compact();
    }

    // 2. Hàm đọc tên khách hàng (username) từ chiếc Thẻ từ
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 3. Hàm kiểm tra xem Thẻ từ có hợp lệ với khách này không và đã hết hạn chưa
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Lấy thời gian sống còn lại của Token để gán TTL vào Redis
    public long getRemainingExpirationTime(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        long diff = expiration.getTime() - System.currentTimeMillis();
        return diff > 0 ? diff : 0;
    }

    // Hàm tạo Refresh Token (thời gian sống dài hơn, ví dụ 7 ngày)
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 Ngày
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
