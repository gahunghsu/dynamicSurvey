package com.example.demo.service;

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

import jakarta.transaction.Transactional;

@Service
public class SurveyService {

	@Autowired
	private SurveyRepository surveyRepository;
	
	/*
	 * 儲存或更新問卷
	 * */
	@Transactional
	public AppResponse<SurveyDTO> saveSurvey(SurveyDTO dto) {
		Survey survey = (dto.getId() != null) ? surveyRepository.findById(dto.getId()).orElse(new Survey()) : new Survey();
		
		survey.setTitle(dto.getTitle());
		survey.setDescription(dto.getDescription());
		survey.setStartDate(dto.getStartDate());
		survey.setEndDate(dto.getEndDate());
		survey.setStatus(dto.getStatus());
		
		survey.getQuestions().clear(); // 清除現有題目關聯
		for(QuestionDTO questionDTO : dto.getQuestions()) {
			Question question = new Question();
			question.setSurvey(survey);
			question.setTitle(questionDTO.getTitle());
			question.setType(questionDTO.getType());
			question.setRequired(questionDTO.isRequired());
			question.setOrderIndex(questionDTO.getOrderIndex());
			
			if(questionDTO.getOptions() != null) {
				for(OptionDTO optionDTO : questionDTO.getOptions()) {
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
