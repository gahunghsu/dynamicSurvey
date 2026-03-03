package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【魔法手環信封】
 * 櫃檯核對完成後，會把這個包含「JWT 手環」的信封交給你。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    // 這就是你的魔法手環。在園內活動時，請務必帶著它！
    private String token;
}
