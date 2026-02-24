package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.SurveyResponse;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
	boolean existsBySurveyIdAndEmail(Long surveyId, String email);
}
