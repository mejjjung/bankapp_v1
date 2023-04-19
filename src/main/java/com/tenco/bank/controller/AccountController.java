package com.tenco.bank.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.DepositFormDto;
import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.dto.WithdrawFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.handler.exception.UnAuthorizedExcetion;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private HttpSession session;
	
	@Autowired
	private AccountService accountService;
	
	/**
	 * 계좌 목록 페이지
	 * @return 목록 페이지 이동
	 */

	@GetMapping({"/list","/"}) // 다중으로 설정 하려면 {}
	public String list(Model model) {
		
		// 인증검사 처리
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		if(principal == null) {
			throw new UnAuthorizedExcetion("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		
		
		// 1. 
		List<Account> accountList = accountService.readAccountList(principal.getId());
		if(accountList.isEmpty()) {
			model.addAttribute("accountList",null);
		} else {
			model.addAttribute("accountList",accountList);
		}
		// View 화면으로 데이터를 내려 주는 기술
		// Model 과 ModelAndView
		
		return "/account/list";
	}
	
	// 출금 페이지
	@GetMapping("/withdraw")
	public String withdraw() {
		
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		if(principal == null) {
			throw new UnAuthorizedExcetion("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		return "/account/withdrawForm";
	}
	
	// 출금 처리 기능
	@PostMapping("/withdraw-proc")
	public String withdrqwProc(WithdrawFormDto withdrawFormDto) {
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		if(principal == null) {
			throw new UnAuthorizedExcetion("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		
		if(withdrawFormDto.getAmount() == null) {
			throw new CustomRestfullException("금액을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		if(withdrawFormDto.getAmount().longValue() <= 0) {
			throw new CustomRestfullException("출금액이 0원 이하일 수는 없습니다.", HttpStatus.BAD_REQUEST);
		}
		
		if(withdrawFormDto.getWAccountNumber() == null || withdrawFormDto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfullException("계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		
		if(withdrawFormDto.getWAccountPassword() == null || withdrawFormDto.getWAccountPassword().isEmpty()) {
			throw new CustomRestfullException("계좌 비밀 번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		
		// todo 서비스 호출 예정 
		accountService.updateAccountWithdraw(withdrawFormDto, principal.getId());
		return "redirect:/account/list";
	}
	
	
	
	
	// 입금 페이지
	@GetMapping("/deposit")
	public String deposit() {
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		if(principal == null) {
			throw new UnAuthorizedExcetion("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		return "/account/depositForm";
	}
	
	// 이체 페이지
	@GetMapping("/transfer")
	public String transfer() {
		return "/account/transferForm";
	}
	
	// 이체 처리 기능
	@PostMapping("/deposit-proc")
	public String depositProc(DepositFormDto depositFormDto) {
		
		if(depositFormDto.getAmount() == null) {
			throw new CustomRestfullException("금액을 입력해 주세요", HttpStatus.BAD_REQUEST);
		}
		
		if(depositFormDto.getAmount().longValue() <= 0) {
			throw new CustomRestfullException("입금 금액이 0원 이하일 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		
		if(depositFormDto.getDAccountNumber() == null || depositFormDto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfullException("계좌 번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		// todo 
		// 서비스 호출
		accountService.updateAccountDeposit(depositFormDto);
		return "redirect:/account/list";
	}
	
	
	// 계좌 생성 페이지
	@GetMapping("/save")
	public String save() {
		// 인증 검사 처리 
		User user = (User)session.getAttribute(Define.PRINCIPAL);
		if(user == null) {
			throw new UnAuthorizedExcetion("로그인 먼저 해주세요",HttpStatus.UNAUTHORIZED);
		}
		
		return "/account/saveForm";
	}
	

	/**
	 * 계좌 생성
	 * 인증검사 
	 * 유효성 검사 처리 - 0원 입력 가능, 마이너스 입력 불가
	 * @param saveFormDto
	 * @return 계좌 목록 페이지 
	 */
	@PostMapping("/save-proc")
	public String saveProc(SaveFormDto saveFormDto) {
		User user = (User)session.getAttribute(Define.PRINCIPAL);
		if(user == null) {
			throw new UnAuthorizedExcetion("로그인 먼저 해주세요",HttpStatus.UNAUTHORIZED);
		}
		
		// 유효성 검사 하기 
		if(saveFormDto.getNumber() == null || saveFormDto.getNumber().isEmpty()) {
			throw new CustomRestfullException("계좌 번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		
		if(saveFormDto.getPassword() == null || saveFormDto.getPassword().isEmpty()) {
			throw new CustomRestfullException("계좌 비밀 번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		
		if(saveFormDto.getBalance() == null || saveFormDto.getBalance() < 0) {
			throw new CustomRestfullException("잘못된 금액 입니다", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 호출 
		accountService.createAccount(saveFormDto, user.getId());
		return "redirect:/account/list";
	}
	
	// 계좌 상세 보기 페이지
	@GetMapping("/detail")
	public String detail() {
		return "";
	}
}
