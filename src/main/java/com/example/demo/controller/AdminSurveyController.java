package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.SurveyDTO;
import com.example.demo.service.SurveyService;
import com.example.demo.vo.AppResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/surveys")
public class AdminSurveyController {

	@Autowired
	private SurveyService surveyService;
	
	@PostMapping
	public AppResponse<?> createSurvey(@RequestBody @Valid SurveyDTO surveyDTO) {
		return surveyService.saveSurvey(surveyDTO);
	}
	
	@PostMapping("/{id}")
	public AppResponse<?> updateSurvey(@PathVariable Long id, @RequestBody @Valid SurveyDTO surveyDTO) {
		surveyDTO.setId(id); // 確保DTO中包含ID以便更新
		return surveyService.saveSurvey(surveyDTO);
	}
}
