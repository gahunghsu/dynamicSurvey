package com.example.demo.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 【設施門口的魔法感應器】
 * 這是每一項設施門口都會有的感應器。
 * 每次有人想玩設施（發送請求）時，它都會攔截下來，並說：「請先感應你的魔法手環 (MagicBand)！」
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * 【安檢核心邏輯】
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 第一步：從遊客遞過來的封包中，尋找那個標註著 "Bearer ..." 的魔法手環
            String jwt = getJwtFromRequest(request);

            // 第二步：如果找到了手環，且感應器的綠燈亮了（驗證過期與真偽）
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 第三步：讀取手環裡的資料，知道這位遊客是誰
                String email = tokenProvider.getUserEmailFromToken(jwt);

                // 第四步：去後台檔案室 (UserDetailsService) 調閱他的詳細權限
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                
                // 第五步：製作一張「當前遊客活動證書」
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 第六步：正式把這位遊客放進「園區活動名單」中 (SecurityContext)
                // 這樣接下來的設施主管就知道你是誰了。
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // 手環感應異常，或是壞掉了
            logger.error("Could not set user authentication in Disney security context", ex);
        }

        // 檢查完畢，不論你有沒有手環，我們都讓你「繼續往下走」
        // 但如果接下來的區域需要手環，那邊的安全地圖 (SecurityConfig) 會把你攔截下來。
        filterChain.doFilter(request, response);
    }

    /**
     * 【從請求頭部提取手環內容】
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // 通常手環會藏在名為 "Authorization" 的口袋裡，且開頭會寫著 "Bearer "
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
