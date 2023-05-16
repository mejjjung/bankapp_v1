package com.tenco.bank.dto;



import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class SignUpFormDto {
	
	private String username;
	private String password;
	private String fullname;
	// private MultipartFile[] 다중처리 배열로 사용하자. 
	private MultipartFile file; // file은 name file 속성과 일치 시켜야 함
	private String originFileName; // 원래 이미지 명
	private String uploadFileName; // 실제 업로드된 이미지명
	
	
}
