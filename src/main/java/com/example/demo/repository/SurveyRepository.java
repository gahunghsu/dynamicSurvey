package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Survey;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
	/**
	* [教學重點] 多條件動態篩選
	* 支援管理員根據標題關鍵字、日期區間進行搜尋。
	*/
	@Query("SELECT s FROM Survey s WHERE " +
	"(:title IS NULL OR s.title LIKE %:title%) AND " +
	"(:startDate IS NULL OR s.startDate >= :startDate) AND " +
	"(:endDate IS NULL OR s.endDate <= :endDate)")
	List<Survey> findByFilters(@Param("title") String title,
	@Param("startDate") LocalDate startDate,
	@Param("endDate") LocalDate endDate);
}
