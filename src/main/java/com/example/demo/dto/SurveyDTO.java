package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SurveyDTO {
	private Long id; // 問卷ID (更新時需要提供)

	@NotBlank(message = "問卷標題不能為空")
	@Size(max = 75, message = "問卷標題不能超過75字")
	private String title; // 問卷標題

	@Size(max = 255, message = "問卷描述不能超過255字")
	private String description; // 問卷描述
	
	@NotNull(message = "問卷開始日期不能為空")
	private LocalDate startDate; // 問卷開始日期
	
	@NotNull(message = "問卷結束日期不能為空")
	private LocalDate endDate; // 問卷結束日期
	
	@NotBlank(message = "問卷狀態不能為空")
	private String status; // 狀態: 是否發布(草稿 - Draft / 已發布 - Published)
	
	private boolean hasResponses; // 是否有回應 (用於前端顯示是否可以編輯問卷)
	
	@Valid
	@NotNull(message = "題目列表不能為空")
	@Size(min = 1, message = "問卷至少需要一個題目")
	private List<QuestionDTO> questions; // 題目列表
}
