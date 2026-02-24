package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.SurveyService;
import com.example.demo.vo.AppResponse;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {
	@Autowired
	SurveyService surveyService;

//	@GetMapping
//	public AppResponse<?> getActiveSurveys() {
//		return surveyService.getActiveSurveys();
//	}

	@GetMapping
	public AppResponse<?> getPublishedSurveys() {
		return surveyService.getPublishedSurveys();
	}

	/**
	 * [功能] 取得單一問卷詳情 [技術細節] 明確指定 PathVariable 名稱為 "id"。
	 */
	@GetMapping("/{id}")
	public AppResponse<?> getSurveyDetails(@PathVariable("id") Long id) {
		return surveyService.getSurveyDetails(id);
	}
}
