package com.tenco.bank.controller;

import lombok.Data;

@Data
public class TransferFormDto {
	private Long amount;
	private String wAccountNumber;
	private String wAccountPassword;
	private String dAccountNumber;
}
