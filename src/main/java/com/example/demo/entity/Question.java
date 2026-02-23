package com.example.demo.entity;

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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "questions")
@Data
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 75)
	private String title; // 題目標題
	
	@Column(nullable = false)
	private String type; // 例如 "單選", "多選", "簡答"
	
	@Column(nullable = false)
	private boolean required; // 是否必填
	
	@Column(nullable = false)
	private int orderIndex; // 題目順序
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_id", nullable=false)
	private Survey survey; // 多對一(多個題目對應一張問卷)
	
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("orderIndex ASC") // 根據 orderIndex 升序排序選項
	private List<Option> options = new ArrayList<>(); // 一對多(一個題目有多個選項)
}
