package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.SurveyResponse;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
	
	List<SurveyResponse> findBySurveyId(Long surveyId);
	List<SurveyResponse> findBySurveyIdOrderByIdDesc(Long surveyId);
	boolean existsBySurveyIdAndEmail(Long surveyId, String email);
}
