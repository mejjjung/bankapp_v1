package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.model.Account;

@Service // IoC 대상 + 싱글톤으로 관리
public class AccountService {

	@Autowired // DI
	private AccountRepository accountRepository;
	
	/**
	 * 계좌 생성 기능
	 * @param saveFormDto
	 * @param principal
	 */
	@Transactional
	public void createAccount(SaveFormDto saveFormDto, Integer principal) {
		Account account = new Account();
		account.setNumber(saveFormDto.getNumber());
		account.setPassword(saveFormDto.getPassword());
		account.setBalance(saveFormDto.getBalance());
		account.setUserId(principal);
		int resultRowCount = accountRepository.insert(account);
		if(resultRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// 계좌 목록 보기 기능
	@Transactional
	public List<Account> readAccountList(Integer userId){
		List<Account> list = accountRepository.findByUserId(userId);
		
		return list;
	}
}
