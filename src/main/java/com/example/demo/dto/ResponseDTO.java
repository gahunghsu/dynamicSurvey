package com.example.demo.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * 提交問卷的資料傳輸物件
 * */
@Data
public class ResponseDTO {
	private Long surveyId; // 問卷ID
	
	@NotBlank(message = "回答者姓名不能為空")
	private String name; // 回答者姓名
	
	@NotBlank(message = "回答者電話不能為空")
	private String phone; // 回答者電話
	
	@NotBlank(message = "回答者電子郵件不能為空")
	private String email; // 回答者電子郵件
	
	private Integer age; // 年齡
	
	private List<AnswerDTO> answers; // 回答列表 (每個回答對應一個題目)
}
