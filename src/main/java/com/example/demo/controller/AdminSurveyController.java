package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.SurveyDTO;
import com.example.demo.service.SurveyService;
import com.example.demo.vo.AppResponse;

import jakarta.servlet.http.HttpSession;
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

	@PutMapping("/{id}")
	public AppResponse<?> updateSurvey(@PathVariable("id") Long id, @RequestBody @Valid SurveyDTO surveyDTO) {
		surveyDTO.setId(id); // 確保DTO中包含ID以便更新
		return surveyService.saveSurvey(surveyDTO);
	}

	@GetMapping
	public AppResponse<?> getSurveys(@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		// 將篩選條件傳交給 Service 處理業務查詢
		return surveyService.getSurveysByAdmin(title, startDate, endDate);
	}

	@GetMapping("/{id}")
	public AppResponse<?> getSurveyById(@PathVariable("id") Long id) {
		return surveyService.getSurveyDetails(id);
	}

	@GetMapping("/{id}/stats")
	public AppResponse<?> getSurveyStats(@PathVariable("id") Long id) {
		return surveyService.getSurveyStats(id);
	}

	/**
	 * [功能] 取得該問卷的所有填寫者清單
	 */
	@GetMapping("/{id}/responses")
	public AppResponse<?> getSurveyResponses(@PathVariable("id") Long id) {
		return surveyService.getSurveyResponses(id);
	}

	/**
	 * [功能] 取得單一作答詳細內容 [路徑] /api/admin/surveys/response-detail/{responseId}
	 */
	@GetMapping("/response-detail/{responseId}")
	public AppResponse<?> getResponseDetail(@PathVariable("responseId") Long responseId) {
		return surveyService.getResponseDetail(responseId);
	}

	/**
	 * [功能] 1. 編輯問卷暫存至 Session
	 */
	@PostMapping("/session-store")
	public AppResponse<?> storeSurveyInSession(@RequestBody SurveyDTO surveyDTO, HttpSession session) {
		return surveyService.saveAdminSurveyToSession(surveyDTO, session);
	}

	/**
	 * [功能] 2. 從 Session 取得編輯中的問卷
	 */
	@GetMapping("/session-get")
	public AppResponse<?> getSurveyFromSession(HttpSession session) {
		return surveyService.getAdminSurveyFromSession(session);
	}

	/**
	 * [功能] 3. 確認提交問卷並決定是否發佈
	 */
	@PostMapping("/confirm-commit")
	public AppResponse<?> confirmSurveyCommit(@RequestParam(name = "isPublish") boolean isPublish,
			HttpSession session) {
		return surveyService.commitAdminSurveyFromSession(isPublish, session);
	}

	/**
	 * [功能] 刪除問卷
	 * -------------------------------------------------------------------------
	 * 【技術細節】 1. @DeleteMapping: 指定使用 DELETE 方法。
	 */
	@DeleteMapping("/{id}")
	public AppResponse<?> deleteSurvey(@PathVariable("id") Long id) {
		return surveyService.deleteSurvey(id);
	}
}
