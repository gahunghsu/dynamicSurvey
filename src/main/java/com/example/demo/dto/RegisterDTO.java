package com.example.demo.dto;

import lombok.Data;

/**
 * 【迪士尼新遊客註冊表】
 * 第一次來到園區？請填寫這份精美的表格，我們將為您建立專屬檔案。
 */
@Data
public class RegisterDTO {
    // 你的名字，讓我們知道該怎麼稱呼你
    private String name;
    
    // 你的電子郵件，這是你在迪士尼唯一的通訊 ID
    private String email;
    
    // 你自訂的入園暗號
    private String password;
    
    // 聯絡電話，以便通知活動資訊
    private String phone;
    
    // 年齡資訊，幫助我們推薦適合的設施
    private Integer age;
}
