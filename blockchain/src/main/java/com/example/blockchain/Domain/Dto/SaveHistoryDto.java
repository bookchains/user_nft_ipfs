package com.example.blockchain.Domain.Dto;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class SaveHistoryDto {
    private BigInteger tokenId;
    private BigInteger dealId;
}
