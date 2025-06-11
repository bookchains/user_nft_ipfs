package com.example.blockchain.Domain.Dto;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class transferRequsetDto {
    private BigInteger tokenId;
    private BigInteger price;
}
