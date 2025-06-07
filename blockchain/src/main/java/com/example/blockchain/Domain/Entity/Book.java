package com.example.blockchain.Domain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @Column
    private BigInteger nftId;

    @Column
    private String title;

    @Column
    private String author;

    @Column
    private Integer price;

    @Column
    private String img;

    @Column
    private String isbn;

    @Column
    private String uri;



    public Book(BigInteger nftId, String title, String author, int price, String img, String uri, String isbn) {
        this.nftId = nftId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.img = img;
        this.uri = uri;
        this.isbn = isbn;
    }
}
