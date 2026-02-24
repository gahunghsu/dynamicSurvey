package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.OptionDTO;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.SurveyDTO;
import com.example.demo.entity.Option;
import com.example.demo.entity.Question;
import com.example.demo.entity.Survey;
import com.example.demo.repository.SurveyRepository;
import com.example.demo.vo.AppResponse;
import com.example.demo.vo.RspCode;

import jakarta.transaction.Transactional;

@Service
public class SurveyService {

	@Autowired
	private SurveyRepository surveyRepository;

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
