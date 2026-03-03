package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;

/**
 * 【迪士尼後台辦公室】
 * 這裡是樂園的決策核心。負責管理遊客通訊錄、核發手環、
 * 以及各種安全檢查任務。
 */
@Service
public class AuthService {

    // 負責管理所有設施保全的經理
    @Autowired
    private AuthenticationManager authenticationManager;

    // 樂園的歷史遊客檔案櫃
    @Autowired
    private UserRepository userRepository;

    // 專門幫遊客密碼「上鎖」的工具
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 魔法手環 (MagicBand) 的製作與燒錄機
    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * 【核對資料並製作手環】
     */
    public String login(LoginDTO loginDTO) {
        // 第一步：經理親自核對你的「身分證」與「暗號」
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        // 第二步：核對無誤，將你的狀態標註為「已在園內活動中」
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        userRepository.findByEmail(loginDTO.getEmail()).ifPresent(user -> {
            if (userRepository.count() == 1 && "USER".equals(user.getRole())) {
                user.setRole("ADMIN");
                userRepository.save(user);
                System.out.println("Disney Note: Promoted the first guest [" + user.getEmail() + "] to Park Manager.");
            }
        });

        // 第三步：啟動手環製作機，把你的身分燒錄進去，回報給票務櫃檯
        return tokenProvider.generateToken(authentication);
    }

    /**
     * 【新遊客建檔】
     */
    public String register(RegisterDTO registerDTO) {
        // 先查查檔案，看看這名字（Email）是不是有人用了
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered in Disney system.");
        }

        // 幫新朋友建檔
        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        
        // 重點：密碼要像米奇寶藏一樣加密，不能直接存在資料庫！
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setAge(registerDTO.getAge());
        
        if (userRepository.count() == 0) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        // 存入檔案櫃，永續保存
        userRepository.save(user);
        
        // 建檔完畢，直接跳轉到「領手環」程序，祝你玩得開心
        return login(new LoginDTO() {{
            setEmail(registerDTO.getEmail());
            setPassword(registerDTO.getPassword());
        }});
    }
}
