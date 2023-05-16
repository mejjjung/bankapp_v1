package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

@Configuration // IoC 등록 - 2개 이상 빈으로 등록 될 때 사용 
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired // DI 처리 해주기 
	private AuthInterceptor authInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor).addPathPatterns("/account/**");// path 더 추가 하는 방법
		
		// 인터셉터 등록
//		registry.addInterceptor(new AuthInterceptor()); // 2. 인터셉터 추가 방법
	}
	
	// 리소스 등록 처리
	// 컴퓨터에 위치한 Resouce를 활용하는 방법(프로젝트 외부 폴더 접근 방법)
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/uploads/**").addResourceLocations("file:///C:\\spring_upload\\bank\\upload/");
	}
}
