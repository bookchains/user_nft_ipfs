package com.example.blockchain.Repository;

import com.example.blockchain.Domain.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, BigInteger> {
    Book findByNftId(@Param("nfrID") BigInteger nftID);
    List<Book> findBooksByIsbn(@Param("isbn") String isbn);
}
