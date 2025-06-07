package com.example.blockchain.Domain.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class TokenData {
    private BigInteger tokenId;
    private String uri;

    public TokenData(BigInteger tokenId, String uri) {
        this.tokenId = tokenId;
        this.uri = uri;
    }
}
