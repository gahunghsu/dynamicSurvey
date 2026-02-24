package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class AnswerDTO {
	private Long questionId; // 題目ID
	// 選項ID列表 (單選題只有一個選項ID，多選題可以有多個選項ID)
	private List<Long> optionIds; 
	private String answerText; // 簡答題的文字回答 (如果是簡答題，則提供此欄位)
}
