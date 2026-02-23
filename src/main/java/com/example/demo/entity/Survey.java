package com.example.demo.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "surveys")
@Data
public class Survey {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 75)
	private String title; // 問卷標題
	
	@Column(length = 255)
	private String description; // 問卷描述
	
	@Column(nullable = false)
	private LocalDate startDate; // 問卷開始日期
	
	@Column(nullable = false)
	private LocalDate endDate; // 問卷結束日期
	
	@Column(nullable = false)
	private String status; // 狀態: 是否發布(草稿 - Draft / 已發布 - Published)
	
	// 一對多(一張問卷有多個題目)
	// mappedBy = "survey" : 指定在 Question 實體中對應的屬性名稱
	// cascade = CascadeType.ALL : 問卷的增刪改操作會自動傳遞到相關的題目
	// orphanRemoval = true : 當題目從問卷中移除時，自動刪除該題目
	@OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("orderIndex ASC") // 根據 orderIndex 升序排序題目
	private List<Question> questions = new ArrayList<>();
}
