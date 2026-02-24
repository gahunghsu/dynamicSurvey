package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "response_answers")
@Data
public class ResponseAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 答案ID
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable=false)
	private SurveyResponse surveyResponse; // 多對一(多個答案對應一個回覆)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable=false)
    private Question question; // 對應的題目
    
    @ManyToMany
    @JoinTable(
		name = "answer_selected_options",
		joinColumns = @JoinColumn(name = "answer_id"),
		inverseJoinColumns = @JoinColumn(name = "option_id")
	)
    private List<Option> selectedOptions = new ArrayList<>(); // 選擇的選項列表 (多選題可能有多個選項)
    
    @Column(columnDefinition = "TEXT")
    private String answerText; // 文本答案 (填空題使用)
}
