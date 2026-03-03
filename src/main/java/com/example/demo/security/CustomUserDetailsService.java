package com.example.demo.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

/**
 * 【迪士尼歷史遊客紀錄員】
 * 這位管理員專門負責去翻閱那本超厚重的「歷史遊客通訊錄」。
 * 當有人要入園時，主管會請他去確認：這名遊客真的有在我們的名單上嗎？
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 【依信箱查閱遊客檔案】
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 去檔案櫃 (Repository) 找找看有沒有這個信箱
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Disney system has no record for : " + email));

        // 如果找到了，就把他的「身分、密碼、以及他在園區的權限」打包交給主管
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                // 幫他在權限前面貼上「ROLE_」標籤，這是迪士尼（Spring Security）的官方格式
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
