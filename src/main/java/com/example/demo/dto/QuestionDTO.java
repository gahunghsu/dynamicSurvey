package com.example.demo.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionDTO {
	private Long id; // 題目ID (更新時需要提供)

	@NotBlank(message = "題目文字不能為空")
	@Size(max = 75, message = "題目文字不能超過75字")
	private String title; // 題目文字
	
	@NotBlank(message = "題目類型不能為空")
	private String type; // 例如 "單選", "多選", "簡答"
	
	private boolean required; // 是否必填

	private int orderIndex; // 題目順序
	
	private List<OptionDTO> options; // 選項列表 (單選、多選題需要提供選項資料)
}
