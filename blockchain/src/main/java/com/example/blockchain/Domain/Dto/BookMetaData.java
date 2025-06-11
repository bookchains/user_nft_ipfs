package com.example.blockchain.Domain.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class BookMetaData {
    private String title;
    private String author;
    private int price;
    private List<String> img;
    private String publisher;
    private String isbn;
    private boolean trade;
    private TradeMethod tradeMethod;
    private int postPrice;
    private String tradePlace;


    private String description;
    private String status;
    private String publishDate;


    public boolean isTrade() {
        return trade;
    }
}
