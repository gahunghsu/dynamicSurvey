package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/*
 * 問卷回覆
 * */
@Entity
@Table(name = "survey_responses")
@Data
public class SurveyResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 回覆ID
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_id", nullable=false)
	private Survey survey; // 多對一(多個回覆對應一張問卷)
	
	@Column(nullable = false)
	private String name; // 回覆者姓名
	
	@Column(nullable = false)
	private String email; // 回覆者電子郵件
	
	@Column(nullable = false)
	private String phone; // 回覆者電話
	
	@Column
	private Integer age; // 回覆者年齡
	
	@Column(nullable = false)
	private LocalDateTime submittedAt; // 回覆提交時間
	
	@OneToMany(mappedBy = "surveyResponse", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ResponseAnswer> answers = new ArrayList<>(); // 回覆的答案列表 (每個答案對應一個題目)
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable=true)
	private User user; // 回覆者 (多對一，回覆者可以是註冊使用者，也可以是匿名回覆)
}
