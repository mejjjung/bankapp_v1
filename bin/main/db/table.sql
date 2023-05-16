-- 유저 테이블 설계해 보기
CREATE TABLE user_tb(
	id int AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) not null UNIQUE,
    password VARCHAR(30) not null, 
    fullname VARCHAR(50) not null,
    created_at TIMESTAMP not null DEFAULT now()
);

-- 사용자의 계좌 정보 테이블 설계 
CREATE TABLE account_tb(
	id int AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(30) not null UNIQUE,
    password VARCHAR(20) not null,
    balance BIGINT not null COMMENT '계좌 잔액',
    user_id int,
    created_at TIMESTAMP not null DEFAULT now()
);

-- 입금 내역 저장
-- 출금 내역 저장
-- 사용자 간 이체 내역 저장


-- 사용자들의 history 테이블 설계 
-- BIGINT 8인트 크기에 정수형
-- 조(10에 12) --경(10에 16승) --해(10에 20승)
-- 자(10에 24) --양(10에 28)

CREATE TABLE history_tb(
	id int AUTO_INCREMENT PRIMARY KEY COMMENT '거래 내역',
    amount BIGINT not null COMMENT '거래 금액',
    w_account_id int COMMENT '출금 계좌 id',
    d_account_id int COMMENT '입금 계좌 id',
    w_balance BIGINT COMMENT '출금 요청된 계좌에 잔액',
    d_balance BIGINT COMMENT '입금 요청된 계좌에 잔액',
    created_at TIMESTAMP not null DEFAULT now()
);