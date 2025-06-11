package com.example.blockchain.Status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    MEMBER_EXISTS("이미 가입된 이메일입니다."),
    NO_SUCH_ALGORITHM("알 수 없는 암호화 알고리즘입니다."),
    UNABLE_TO_SEND_EMAIL("이메일을 보낼 수 없습니다.");

    private final String message;
}
