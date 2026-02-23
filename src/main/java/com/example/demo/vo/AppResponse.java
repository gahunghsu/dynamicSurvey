package com.example.demo.vo;

import lombok.Data;

//Ctrl + Shift + O 來自動匯入需要的類別
@Data
public class AppResponse<T> {
	private int code; // 狀態碼
	private String message; // 訊息說明
	private T data; // 資料內容 (可能是單一物件、列表或者是null...)
	
	/* 建構子 : 僅包含 HTTP 狀態碼和訊息說明(api失敗、無回傳資料的情況) */
	public AppResponse(RspCode rspCode) {
		this.code = rspCode.getCode();
		this.message = rspCode.getMessage();
	}
	
	/* 建構子 : 包含 HTTP 狀態碼、訊息說明和資料 */
	public AppResponse(RspCode rspCode, T data) {
		this.code = rspCode.getCode();
		this.message = rspCode.getMessage();
		this.data = data;
	}
	
	/* 靜態方法 : 用於快速建立成功的回應物件，並包含資料 */
	public static <T> AppResponse<T> success(T data) {
		return new AppResponse<>(RspCode.SUCCESS, data);
	}
	
	/* 靜態方法 : 用於快速建立失敗的回應物件，僅包含狀態碼和訊息說明 */
	public static <T> AppResponse<T> error(RspCode rspCode) {
		return new AppResponse<>(rspCode);
	}
	
	/* 靜態方法 : 用於自訂義錯誤訊息(例如顯示具體的驗證錯誤說明) */
	public static <T> AppResponse<T> error(RspCode rspCode, String errorMessage) {
		AppResponse<T> response = new AppResponse<>(rspCode);
		response.setMessage(errorMessage); // 覆蓋預設訊息說明
		return response;
	}
}
