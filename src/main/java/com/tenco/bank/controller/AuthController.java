package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.tenco.bank.dto.OAuthToken;

@Controller
public class AuthController {

	@Value("${tenco.key}")
	private String tencoKey;
	
	@GetMapping("/auth/kakao/callback")
	@ResponseBody // 이녀석은 viewResolver가 아니라 데이터를 반환한다. 
	public String kakaoCallbackCode(@RequestParam String code) {
		
		System.out.println("tencoKey: " + tencoKey);
		System.out.println("카카오가 보낸 인가 코드 확인");
		System.out.println("code : " + code );
		
		// 서버 to 서버
		RestTemplate restTemplate = new RestTemplate();
		// header 생성 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// body 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "5672ac917cf9ee569430446103d38b9e");
		params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
		params.add("code", code);
		
		HttpEntity<MultiValueMap<String, String>> kakaoReqEntity = new HttpEntity<>(params, headers);
		
		ResponseEntity<OAuthToken> responseToken =  restTemplate.exchange("https://kauth.kakao.com/oauth/token",
								HttpMethod.POST,
								kakaoReqEntity,
								OAuthToken.class);
		System.out.println(responseToken);
		System.out.println(responseToken.getBody().toString());
		
		// 액세스 토큰 --> 사용자에 정보 (자원요청)
		String userInfo = requestKakaoUserInfo(responseToken.getBody().getAccessToken());
		System.out.println("userInfo : " + userInfo);
		// 카카오 자원서버에서 데이터를 받음!!!
		
		// 우리 서버에 추가 작업해야 할 사항
		
		// 세션 처리 해야 한다 - (선행) 회원 가입 되어 있어야 한다. 
		// user_tb -> username, password, email(선택)
		
		// SELECT 확인 후 
		
		// 회원가입 (중복된 이름 생길 수 있음)
		// 최초 소셜 접근 사용자
		// 카카오 닉네임으로 -> username 저장
		// password (필수) <-- 임의값으로 DB에 저장해야 한다. 
		
		// 로그인 처리 - 세션 발급 
		
		return userInfo;
	}
	
	/*
	 * GET/POST /v2/user/me HTTP/1.1 Host: kapi.kakao.com Authorization: Bearer
	 * Authorization: Bearer ${ACCESS_TOKEN}/KakaoAK ${APP_ADMIN_KEY}
	 * application/x-www-form-urlencoded;charset=utf-8
	 */
	private String requestKakaoUserInfo(String oAuthToken) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		// Bearer 다음에 반드시 한칸 띄어쓰기 (규칙!!!!)
		headers.add("Authorization", "Bearer " + oAuthToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 바디 생략 ..
		
		HttpEntity<String> profilReqEntity = new HttpEntity<>(headers);
		// https://kapi.kakao.com/v2/user/me
		ResponseEntity<String> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me",
					HttpMethod.GET,
					profilReqEntity,
					String.class
					);
		return response.getBody().toString();
	}
}
