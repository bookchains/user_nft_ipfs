package com.example.blockchain.Domain.Dto;

import com.example.blockchain.Validation.CustomEmail;
import lombok.Getter;

@Getter
public class EmailVerifyRequestDto {
    @CustomEmail
    private String email;
    private String code;
}
