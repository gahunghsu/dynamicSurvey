package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.service.SurveyService;
import com.example.demo.vo.AppResponse;

import jakarta.servlet.http.HttpSession;

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

	/**
	 * [功能] 1. 暫存作答資料至 Session (進入確認頁前呼叫)
	 */
	@PostMapping("/session-store")
	public AppResponse<?> storeInSession(@RequestBody ResponseDTO submission, HttpSession session) {
		return surveyService.saveToSession(submission, session);
	}

	/**
	 * [功能] 2. 從 Session 取得暫存資料 (確認頁唯讀顯示)
	 */
	@GetMapping("/session-get")
	public AppResponse<?> getFromSession(HttpSession session) {
		return surveyService.getFromSession(session);
	}

	/**
	 * [功能] 3. 正式提交問卷 (從 Session 轉存資料庫)
	 */
	@PostMapping("/confirm")
	public AppResponse<?> confirmSubmit(HttpSession session) {
		return surveyService.commitFromSession(session);
	}
}
