package com.example.blockchain.Status;

public class StatusCode {

    public static final int OK = 200;


    public static final int BAD_REQUEST = 400;             // 요청 파라미터 미비
    public static final int UNAUTHORIZED = 401;            // 인증 실패 / 토큰 만료


    public static final int not_found_ISBN = 201;           //isbn 찾을 수 없음

    public static final int NOT_FOUND_DEAL = 202;
}
