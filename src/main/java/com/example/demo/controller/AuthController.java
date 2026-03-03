package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.service.AuthService;
import com.example.demo.vo.AppResponse;
import com.example.demo.vo.RspCode;

/**
 * 【迪士尼票務櫃檯】
 * 這裡是遊客進入迪士尼樂園的第一站。
 * 櫃檯人員（Controller）負責收件，核對沒問題後，就會請後台核發魔法手環。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 【登入入口 - 領取今日魔法手環】
     * 已經有會員身分的遊客，憑信箱與密碼來換取手環。
     * 生活例子：就像你到樂園門口，刷出你的電子購票證明，換取一個感應手環入園。
     */
    @PostMapping("/login")
    public AppResponse<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            // 呼叫後台主管製作一個專屬手環 (Token)
            String token = authService.login(loginDTO);
            return AppResponse.success(new LoginResponseDTO(token));
        } catch (Exception e) {
            // 身分核對失敗，不能隨便放人入園喔
            return AppResponse.error(RspCode.UNAUTHORIZED, "Invalid email or password");
        }
    }

    /**
     * 【註冊入口 - 成為迪士尼新會員】
     * 第一次來的遊客，需要填寫詳細資料建檔。
     * 生活例子：填寫迪士尼官方會員申請表，設定好你的專屬暗號（密碼）。
     */
    @PostMapping("/register")
    public AppResponse<LoginResponseDTO> register(@RequestBody RegisterDTO registerDTO) {
        try {
            // 呼叫主管進行新遊客建檔
            String token = authService.register(registerDTO);
            // 建檔成功，貼心地直接給他手環，祝他玩得愉快
            return AppResponse.success(new LoginResponseDTO(token));
        } catch (RuntimeException e) {
            // 這個信箱已經註冊過了，可能你以前來過？
            return AppResponse.error(RspCode.DUPLICATE_ERROR, e.getMessage());
        } catch (Exception e) {
            // 樂園系統維護中或其他意外
            return AppResponse.error(RspCode.INTERNAL_SERVER_ERROR, "Registration failed");
        }
    }
}
