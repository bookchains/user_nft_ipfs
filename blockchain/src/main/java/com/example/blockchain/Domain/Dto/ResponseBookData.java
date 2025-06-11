package com.example.blockchain.Domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class ResponseBookData {
    private BigInteger tokenId;
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

    private String sellerAccount;





    public ResponseBookData(BigInteger tokenId, BookMetaData book, String sellerAccount) {
        this.tokenId = tokenId;
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.price = book.getPrice();
        this.img = book.getImg();
        this.publisher = book.getPublisher();
        this.isbn = book.getIsbn();
        this.trade = book.isTrade();
        this.tradeMethod = book.getTradeMethod();
        this.postPrice = book.getPostPrice();
        this.tradePlace = book.getTradePlace();

        this.description = book.getDescription();
        this.status = book.getStatus();
        this.publishDate = book.getPublishDate();

        this.sellerAccount = sellerAccount;
    }


}
