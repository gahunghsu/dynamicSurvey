package com.example.demo.vo;

import lombok.Getter;

@Getter
public enum RspCode {
	// 定義狀態碼
	SUCCESS(200, "操作成功"),
	PARAM_ERROR(400, "參數錯誤"),
	UNAUTHORIZED(401, "尚未登入或憑證失效"),
	FORBIDDEN(403, "沒有權限訪問"),
	NOT_FOUND(404, "資源未找到"),
	DUPLICATE_ERROR(409, "資料重複"),	
	INTERNAL_SERVER_ERROR(500, "伺服器錯誤");
	
	//存放對應的HTTP狀態碼
	private final int code;
	
	//存放對應的HTTP說明訊息
	private final String message;
	
	//建構子:用於建立列舉實例並賦值
	RspCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
