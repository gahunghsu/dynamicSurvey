package com.example.demo.dto;

import lombok.Data;

/**
 * 【魔法手環領取單】
 * 當你已經是迪士尼會員，只需填寫信箱與密碼，就能換取今日的手環。
 */
@Data
public class LoginDTO {
    // 你的入園帳號（信箱）
    private String email;
    // 你的秘密暗號（密碼）
    private String password;
}
