package com.example.blockchain.Domain.Dto;

import com.example.blockchain.Validation.CustomEmail;
import lombok.Getter;

@Getter
public class MemberEmailRequestDto {
    @CustomEmail
    private String email;
}
