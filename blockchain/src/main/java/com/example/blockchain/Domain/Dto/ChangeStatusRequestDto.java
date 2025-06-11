package com.example.blockchain.Domain.Dto;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ChangeStatusRequestDto {
    private BigInteger tokenId;
    private BookMetaData book;
}
