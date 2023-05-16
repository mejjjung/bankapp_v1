package com.tenco.bank.controller;

import java.io.File;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tenco.bank.dto.SignInFormDto;
import com.tenco.bank.dto.SignUpFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired // DI 처리
	private UserService userService;

	@Autowired
	private HttpSession session;
	
	// http://localhost:8080/user/sign-up
	@GetMapping("/sign-up")
	public String signUp() {
		// prefix
		// subfix
		return "/user/signUp";
	}
		
	
	/**
	 * 회원 가입 처리 
	 * @param signUpFormDto
	 * @return 리다이렉트 로그인 페이지
	 */
	@PostMapping("/sign-up")
	public String signUpProc(SignUpFormDto signUpFormDto) {
		
		System.out.println(signUpFormDto.getFile().getContentType());
		System.out.println(signUpFormDto.getFile().getOriginalFilename());
		System.out.println(signUpFormDto.getFile().getSize());
		
		// 1. 유효성 검사
//		if(signUpFormDto.getUsername() == null || signUpFormDto.getUsername().isEmpty()) {
//			throw new CustomRestfullException("username을 입력해 주세요", HttpStatus.BAD_REQUEST);
//		}
//		
//		if(signUpFormDto.getPassword() == null || signUpFormDto.getPassword().isEmpty()) {
//			throw new CustomRestfullException("password를 입력해 주세요", HttpStatus.BAD_REQUEST);
//		}
//		
//		if(signUpFormDto.getFullname() == null || signUpFormDto.getFullname().isEmpty()) {
//			throw new CustomRestfullException("fullname을 입력해 주세요", HttpStatus.BAD_REQUEST);
//		}
//		
		// 사용자 프로필 이미지는 옵션 값으로 설정할 예정 
		MultipartFile file = signUpFormDto.getFile();
		if(file.isEmpty() == false) {
			// 사용자가 이미지를 업로드 했다면 기능 구현 해야 함 
			// 파일 사이즈 체크 : 10MB 까지 가능함 기본 설정 
			if(file.getSize() > Define.MAX_FILE_SIZE) {
				throw new CustomRestfullException("파일 크기는 20MB 이상 클 수 없습니다.", HttpStatus.BAD_REQUEST);
			}
			
			// 확장자 검사 가능
			
			try {
				// 파일 저장 기능 구현 - 업로드 파일은 HOST 컴퓨터 다른 폴더로 관리 
				String saveDirectory = Define.UPLOAD_DIRECTORY;
				// 폴더가 없다면 오류 발생 (파일 생성 시)
				File dir = new File(saveDirectory);
				if(dir.exists() == false) {
					dir.mkdirs(); // 폴더가 없으면 폴더 생성 
				}
				
				UUID uuid = UUID.randomUUID();
				String fileName = uuid + "_" + file.getOriginalFilename();
				// 전체 경로를 지정 
				String uploadPath = Define.UPLOAD_DIRECTORY + File.separator +fileName;
				File destination = new File(uploadPath); // 파일 객체 만들기
				// 좀 더 간편한 방법
				file.transferTo(destination);
				
				// 객체 상태 변경(dto)
				signUpFormDto.setOriginFileName(file.getOriginalFilename());
				signUpFormDto.setUploadFileName(fileName);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		// 서비스 호출
		userService.createUser(signUpFormDto);
		
		return "redirect:/user/sign-in";
	}
	
	/**
	 * 로그인 폼
	 * @return 로그인 페이지 
	 */
	@GetMapping("/sign-in")
	public String signIn() {
		
		return "/user/signIn";
	}
	
	/**
	 * 로그인 처리
	 * @param signUpFormDto
	 * @return 메인 페이지 이동 (수정 예정)
	 * 생각해보기 
	 * GET 방식 처리는 브라우저 히스토리에 남겨지기 때문에
	 * 예외적으로 로그인 post 방식으로 처리 한다. (보안)
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto signInFormDto) {
		
		// 1. 유효성 검사 (인증 검사가 더 우선)
		if(signInFormDto.getUsername() == null || signInFormDto.getUsername().isEmpty()) {
			throw new CustomRestfullException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if(signInFormDto.getPassword() == null || signInFormDto.getPassword().isEmpty()) {
			throw new CustomRestfullException("password를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		// todo
		// 서비스 호출 --
		// 세션에 저장 - 사용자 정보
		User principal = userService.signIn(signInFormDto);
		principal.setPassword(null);
		session.setAttribute(Define.PRINCIPAL, principal);
		
		// todo 변경 예정
		return "redirect:/account/list";
	}
	
	@GetMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/user/sign-in";
	}
	
		
}
