package com.example.blockchain.Domain.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String username;
    private String address;
    private String privKey;
}
