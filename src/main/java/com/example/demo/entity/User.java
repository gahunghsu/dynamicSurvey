package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 使用者ID
	
	@Column(nullable = false)
	private String name; // 使用者名稱
	
	@Column(nullable = false, unique = true)
	private String email; // 使用者電子郵件
	
	@Column(nullable = false)
	private String phone; // 使用者電話
	
	private Integer age; // 使用者年齡
	
	@Column(nullable = false)
	private String password; // 使用者密碼 (實際應該加密存儲)
	
	@Column(nullable = false)
	private String role; // 使用者角色 (例如: "USER", "ADMIN")
}
