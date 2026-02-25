package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AnswerDTO;
import com.example.demo.dto.OptionDTO;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.SurveyDTO;
import com.example.demo.entity.Option;
import com.example.demo.entity.Question;
import com.example.demo.entity.ResponseAnswer;
import com.example.demo.entity.Survey;
import com.example.demo.entity.SurveyResponse;
import com.example.demo.repository.SurveyRepository;
import com.example.demo.repository.SurveyResponseRepository;
import com.example.demo.vo.AppResponse;
import com.example.demo.vo.RspCode;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class SurveyService {

	@Autowired
	private SurveyRepository surveyRepository;

	@Autowired
	SurveyResponseRepository responseRepository;

	private static final String SURVEY_SESSION_KEY = "TEMP_SURVEY_RESPONSE";

	private static final String ADMIN_EDIT_SESSION_KEY = "TEMP_ADMIN_SURVEY";

	/*
	 * 儲存或更新問卷
	 */
	@Transactional
	public AppResponse<SurveyDTO> saveSurvey(SurveyDTO dto) {
		Survey survey = (dto.getId() != null) ? surveyRepository.findById(dto.getId()).orElse(new Survey())
				: new Survey();

		survey.setTitle(dto.getTitle());
		survey.setDescription(dto.getDescription());
		survey.setStartDate(dto.getStartDate());
		survey.setEndDate(dto.getEndDate());
		survey.setStatus(dto.getStatus());

		survey.getQuestions().clear(); // 清除現有題目關聯
		for (QuestionDTO questionDTO : dto.getQuestions()) {
			Question question = new Question();
			question.setSurvey(survey);
			question.setTitle(questionDTO.getTitle());
			question.setType(questionDTO.getType());
			question.setRequired(questionDTO.isRequired());
			question.setOrderIndex(questionDTO.getOrderIndex());

			if (questionDTO.getOptions() != null) {
				for (OptionDTO optionDTO : questionDTO.getOptions()) {
					Option option = new Option();
					option.setQuestion(question);
					option.setOptionText(optionDTO.getOptionText());
					option.setOrderIndex(optionDTO.getOrderIndex());
					question.getOptions().add(option);
				}
			}
			survey.getQuestions().add(question);
		}
		Survey savedSurvey = surveyRepository.save(survey);
		return AppResponse.success(convertToDTO(savedSurvey)); // 這裡可以根據需要返回更新後的DTO
	}

	public AppResponse<List<SurveyDTO>> getSurveysByAdmin(String title, LocalDate start, LocalDate end) {
		List<Survey> surveys = surveyRepository.findByFilters(title, start, end);
		return AppResponse.success(surveys.stream().map(s -> {
			SurveyDTO dto = convertToDTO(s);
			return dto;
		}).collect(Collectors.toList()));
	}

	/**
	 * [功能] 取得單一問卷詳情 (填寫用)
	 */
	public AppResponse<SurveyDTO> getSurveyDetails(Long id) {
		return surveyRepository.findById(id).map(s -> AppResponse.success(convertToDTO(s)))
				.orElse(AppResponse.error(RspCode.NOT_FOUND));
	}

	/**
	 * [功能] 取得所有進行中的問卷 【關鍵點】調用 Repository 的自定義查詢，僅回傳符合日期範圍且已發佈的問卷。
	 */
	public AppResponse<List<SurveyDTO>> getActiveSurveys() {
		List<Survey> surveys = surveyRepository.findActiveSurveys(LocalDate.now());
		return AppResponse.success(surveys.stream().map(this::convertToDTO).collect(Collectors.toList()));
	}

	/**
	 * [功能] 取得所有已發佈的問卷
	 */
	public AppResponse<List<SurveyDTO>> getPublishedSurveys() {
		List<Survey> surveys = surveyRepository.findPublishedSurveys();
		return AppResponse.success(surveys.stream().map(this::convertToDTO).collect(Collectors.toList()));
	}

	/**
	 * [功能] 刪除問卷
	 */
	@Transactional
	public AppResponse<?> deleteSurvey(Long id) {
		surveyRepository.deleteById(id);
		return AppResponse.success(null);
	}

	public AppResponse<?> saveToSession(ResponseDTO submission, HttpSession session) {
		if (responseRepository.existsBySurveyIdAndEmail(submission.getSurveyId(), submission.getEmail())) {
			return AppResponse.error(RspCode.DUPLICATE_ERROR, "此 Email 已填寫過本問卷。");
		}
		session.setAttribute(SURVEY_SESSION_KEY, submission);
		return AppResponse.success(null);
	}

	public AppResponse<ResponseDTO> getFromSession(HttpSession session) {
		ResponseDTO data = (ResponseDTO) session.getAttribute(SURVEY_SESSION_KEY);
		if (data == null)
			return AppResponse.error(RspCode.NOT_FOUND);
		return AppResponse.success(data);
	}

	@Transactional
	public AppResponse<?> commitFromSession(HttpSession session) {
		ResponseDTO submission = (ResponseDTO) session.getAttribute(SURVEY_SESSION_KEY);
		if (submission == null)
			return AppResponse.error(RspCode.NOT_FOUND);
		AppResponse<?> response = submitResponse(submission.getSurveyId(), submission);
		if (response.getCode() == 200) {
			session.removeAttribute(SURVEY_SESSION_KEY);
		}
		return response;
	}

	@Transactional
	public AppResponse<?> submitResponse(Long surveyId, ResponseDTO submission) {
		Survey survey = surveyRepository.findById(surveyId).orElse(null);
		if (survey == null)
			return AppResponse.error(RspCode.NOT_FOUND);
		SurveyResponse response = new SurveyResponse();
		response.setSurvey(survey);
		response.setSubmittedAt(LocalDateTime.now());
		response.setName(submission.getName());
		response.setPhone(submission.getPhone());
		response.setEmail(submission.getEmail());
		response.setAge(submission.getAge());
		for (AnswerDTO aDto : submission.getAnswers()) {
			ResponseAnswer answer = new ResponseAnswer();
			answer.setSurveyResponse(response);
			Question question = survey.getQuestions().stream().filter(q -> q.getId().equals(aDto.getQuestionId()))
					.findFirst().orElse(null);
			if (question == null)
				continue;
			answer.setQuestion(question);
			if (question.getType().equals("TEXT")) {
				answer.setAnswerText(aDto.getAnswerText());
			} else {
				List<Option> selected = question.getOptions().stream()
						.filter(o -> aDto.getOptionIds().contains(o.getId())).collect(Collectors.toList());
				answer.setSelectedOptions(selected);
				answer.setAnswerText(selected.stream().map(Option::getOptionText).collect(Collectors.joining(";")));
			}
			response.getAnswers().add(answer);
		}
		responseRepository.save(response);
		return AppResponse.success(null);
	}

	public AppResponse<?> getSurveyStats(Long id) {
		Survey survey = surveyRepository.findById(id).orElse(null);
		if (survey == null)
			return AppResponse.error(RspCode.NOT_FOUND);
		List<SurveyResponse> responses = responseRepository.findBySurveyId(id);
		int totalResponses = responses.size();
		Map<String, Object> stats = new HashMap<>();
		stats.put("surveyId", survey.getId());
		stats.put("surveyTitle", survey.getTitle());
		stats.put("totalResponses", totalResponses);
		List<Map<String, Object>> qStatsList = new ArrayList<>();
		for (Question q : survey.getQuestions()) {
			Map<String, Object> qMap = new HashMap<>();
			qMap.put("questionId", q.getId());
			qMap.put("questionTitle", q.getTitle());
			qMap.put("type", q.getType());
			if (q.getType().equals("TEXT")) {
				qMap.put("textAnswers", responses.stream().flatMap(r -> r.getAnswers().stream())
						.filter(a -> a.getQuestion().getId().equals(q.getId())).map(ResponseAnswer::getAnswerText)
						.filter(Objects::nonNull).collect(Collectors.toList()));
			} else {
				Map<Long, Map<String, Object>> optMap = new HashMap<>();
				for (Option o : q.getOptions()) {
					Map<String, Object> oData = new HashMap<>();
					oData.put("optionText", o.getOptionText());
					oData.put("count", 0);
					optMap.put(o.getId(), oData);
				}
				responses.stream().flatMap(r -> r.getAnswers().stream())
						.filter(a -> a.getQuestion().getId().equals(q.getId()))
						.flatMap(a -> a.getSelectedOptions().stream()).forEach(o -> {
							Map<String, Object> oData = optMap.get(o.getId());
							if (oData != null)
								oData.put("count", (int) oData.get("count") + 1);
						});
				for (Map<String, Object> oData : optMap.values()) {
					double pct = totalResponses > 0 ? ((int) oData.get("count") * 100.0 / totalResponses) : 0;
					oData.put("percentage", Math.round(pct * 10.0) / 10.0);
				}
				qMap.put("optionStats", optMap);
			}
			qStatsList.add(qMap);
		}
		stats.put("questionStats", qStatsList);
		return AppResponse.success(stats);
	}

	public AppResponse<?> getSurveyResponses(Long id) {
		List<SurveyResponse> responses = responseRepository.findBySurveyIdOrderByIdDesc(id);
		
		List<Map<String, Object>> result = new ArrayList<>();
		for(SurveyResponse r: responses) {
			Map<String, Object> map = new HashMap<>();
			map.put("responseId", r.getId());
			map.put("userName", r.getName());
			map.put("userEmail", r.getEmail());
			map.put("submittedAt", r.getSubmittedAt());
			result.add(map);
		}		
		return AppResponse.success(result);
		
//		return AppResponse.success(responses.stream().map(r -> {
//			Map<String, Object> map = new HashMap<>();
//			map.put("responseId", r.getId());
//			map.put("userName", r.getName());
//			map.put("userEmail", r.getEmail());
//			map.put("submittedAt", r.getSubmittedAt());
//			return map;
//		}).collect(Collectors.toList()));
	}

	public AppResponse<?> getResponseDetail(Long responseId) {
		SurveyResponse response = responseRepository.findById(responseId).orElse(null);
		if (response == null)
			return AppResponse.error(RspCode.NOT_FOUND);
		Map<String, Object> result = new HashMap<>();
		result.put("responseId", response.getId());
		result.put("userName", response.getName());
		result.put("submittedAt", response.getSubmittedAt());
		result.put("surveyTitle", response.getSurvey().getTitle());
		List<Map<String, Object>> details = response.getAnswers().stream().map(a -> {
			Map<String, Object> aMap = new HashMap<>();
			aMap.put("questionTitle", a.getQuestion().getTitle());
			aMap.put("type", a.getQuestion().getType());
			aMap.put("answer", a.getAnswerText()); // 多選題已在存入時串接好
			return aMap;
		}).collect(Collectors.toList());
		result.put("details", details);
		return AppResponse.success(result);
	}

	/**
	 * [功能] 管理員編輯問卷暫存至 Session
	 */
	public AppResponse<?> saveAdminSurveyToSession(SurveyDTO surveyDTO, HttpSession session) {
		session.setAttribute(ADMIN_EDIT_SESSION_KEY, surveyDTO);
		return AppResponse.success(null);
	}

	/**
	 * [功能] 管理員從 Session 取得正在編輯的問卷
	 */
	public AppResponse<SurveyDTO> getAdminSurveyFromSession(HttpSession session) {
		SurveyDTO dto = (SurveyDTO) session.getAttribute(ADMIN_EDIT_SESSION_KEY);
		if (dto == null)
			return AppResponse.error(RspCode.NOT_FOUND, "找不到編輯中的資料");
		return AppResponse.success(dto);
	}

	/**
	 * [功能] 管理員正式提交問卷並清空 Session
	 * 
	 * @param isPublish 是否發佈 (true -> PUBLISHED, false -> DRAFT)
	 */
	@Transactional
	public AppResponse<SurveyDTO> commitAdminSurveyFromSession(boolean isPublish, HttpSession session) {
		SurveyDTO dto = (SurveyDTO) session.getAttribute(ADMIN_EDIT_SESSION_KEY);
		if (dto == null)
			return AppResponse.error(RspCode.NOT_FOUND);
		// 根據按鈕決定狀態
		dto.setStatus(isPublish ? "PUBLISHED" : "DRAFT");

		AppResponse<SurveyDTO> response = saveSurvey(dto);
		if (response.getCode() == 200) {
			session.removeAttribute(ADMIN_EDIT_SESSION_KEY);
		}
		return response;
	}

	private SurveyDTO convertToDTO(Survey survey) {
		SurveyDTO dto = new SurveyDTO();
		dto.setId(survey.getId());
		dto.setTitle(survey.getTitle());
		dto.setDescription(survey.getDescription());
		dto.setStartDate(survey.getStartDate());
		dto.setEndDate(survey.getEndDate());
		dto.setStatus(survey.getStatus());

		dto.setQuestions(survey.getQuestions().stream().map(question -> {
			QuestionDTO questionDTO = new QuestionDTO();
			questionDTO.setId(question.getId());
			questionDTO.setTitle(question.getTitle());
			questionDTO.setType(question.getType());
			questionDTO.setRequired(question.isRequired());
			questionDTO.setOrderIndex(question.getOrderIndex());

			questionDTO.setOptions(question.getOptions().stream().map(option -> {
				OptionDTO optionDTO = new OptionDTO();
				optionDTO.setId(option.getId());
				optionDTO.setOptionText(option.getOptionText());
				optionDTO.setOrderIndex(option.getOrderIndex());
				return optionDTO;
			}).collect(Collectors.toList()));

			return questionDTO;
		}).collect(Collectors.toList()));
		return dto;
	}
}
