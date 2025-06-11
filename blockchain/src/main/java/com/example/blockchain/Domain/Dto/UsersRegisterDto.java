package com.example.blockchain.Domain.Dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersRegisterDto {

    private String walletAccount;
    private String name;
    private String nickName;
    private String phone;
    private String address;
    private String email;


}
