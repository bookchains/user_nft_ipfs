package com.example.blockchain.Domain.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class Deal {
    private String buyer;
    private String seller;
    private BigInteger priceWei;
    private String date;

    public Deal(String buyer, String seller, BigInteger priceWei, String date) {
        this.buyer = buyer;
        this.seller = seller;
        this.priceWei = priceWei;
        this.date = date;
    }
}
