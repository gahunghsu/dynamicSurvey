package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OptionDTO {

	private Long id; // 選項ID (更新時需要提供)

	@NotBlank(message = "選項文字不能為空")
	private String optionText; // 選項文字

	private int orderIndex; // 選項順序
}
