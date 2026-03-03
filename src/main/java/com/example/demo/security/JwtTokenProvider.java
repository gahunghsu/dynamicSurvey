package com.example.demo.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * 【魔法手環 (MagicBand) 製作機器】
 * 這是樂園裡最神奇的機器。它負責把遊客的身分，透過「數位燒錄」的方式
 * 變成一串長長的加密文字（Token），並設定好它的壽命。
 */
@Component
public class JwtTokenProvider {

    // 這台機器的專屬「防偽印章」（金鑰）
    @Value("${jwt.secret:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}")
    private String secretKey;

    // 魔法手環的電力（有效期），預設是 24 小時，時間一到，手環就失效囉
    @Value("${jwt.expiration:86400000}") 
    private long jwtExpirationInMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 【燒錄一個新環手環】
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // 像是在做餅乾一樣，把你的名字、發放時間、到期時間打包，最後蓋上「防偽印章」
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 【讀取手環裡的遊客姓名】
     * 感應一下手環，我們就知道這是哪位遊客。
     */
    public String getUserEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 【驗證手環真偽與電力】
     * 確認這個手環是不是我們發出的，以及是不是已經過期（沒電）了。
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            // 如果手環被塗改、或是偽造的、或是電力耗盡，通通回傳 False
            return false;
        }
    }
}
