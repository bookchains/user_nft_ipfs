package com.example.blockchain.Controller;

import com.example.blockchain.Domain.Dto.*;
import com.example.blockchain.Domain.Entity.Book;
import com.example.blockchain.Repository.BookRepository;
import com.example.blockchain.Services.BookServices;
import com.example.blockchain.Services.NFTServices;
import com.example.blockchain.Status.StatusCode;
import com.example.blockchain.jwt.JwtProperties;
import com.example.blockchain.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    @Autowired
    private BookServices bookServices;
    @Autowired
    private NFTServices nftServices;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public BigInteger registerBook(@RequestPart BookMetaData dto, @RequestPart("image") List<MultipartFile> imageFiles, HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String address = jwtUtil.getAddress(token);
        String privateKey = jwtUtil.getPrivateKey(token);

        System.out.println("privateKey = " + privateKey);

        // 1. 이미지 저장
        List<String> savedImagePaths = new ArrayList<>();
        String uploadDir = "C:/upload";  // 예: "C:/upload" 또는 "/var/www/uploads"

        for (MultipartFile image : imageFiles) {
            if (!image.isEmpty()) {
                String originalFilename = image.getOriginalFilename();
                String newFileName = UUID.randomUUID() + "_" + originalFilename;
                File dest = new File(uploadDir, newFileName);
                image.transferTo(dest);

                // Web에서 접근 가능한 경로로 저장 (예: "/uploads/uuid_filename.jpg")
                savedImagePaths.add("/uploads/" + newFileName);
            }
        }

        // 여러 이미지 중 첫 번째 이미지를 대표 이미지로 저장 (단일 경로만 저장하는 경우)
        String representativeImage = savedImagePaths.isEmpty() ? null : savedImagePaths.get(0);

        dto.setImg(savedImagePaths);

        String uri = bookServices.uploadBookMetadata(dto);

        BigInteger tokenId = nftServices.mintBook(uri, address);

        bookRepository.save(new Book(tokenId, dto.getTitle(), dto.getAuthor(), dto.getPrice(), representativeImage, dto.getIsbn(), uri));

        nftServices.listBook(privateKey, tokenId, BigInteger.valueOf(dto.getPrice()));

        return tokenId;
    }


    @GetMapping
    public List<Book> homeBook() throws Exception {

        List<Book> books = bookRepository.findAll();

        return books;
    }

    @GetMapping("/detail/{tokenId}")
    public ResponseBookData bookDetail(@PathVariable BigInteger tokenId, HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String privateKey = jwtUtil.getPrivateKey(token);

        System.out.println("detail");

        String uri = nftServices.getTokenUri(privateKey, tokenId);

        BookMetaData bookMetaData = bookServices.fetchBookMetadataFromIpfs(uri);

        String sellerAccount = nftServices.OwnerOf(privateKey, tokenId);


        return new ResponseBookData(tokenId, bookMetaData, sellerAccount);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Boolean> checkISBN(@PathVariable String isbn){

        if(bookServices.checkISBN(isbn)){
            return ResponseEntity.ok(true);
        }
        else
            return ResponseEntity
                    .status(StatusCode.not_found_ISBN)
                    .body(false);
    }

    @PatchMapping("/{symbol}/status")
    public boolean changeStatus(@RequestBody ChangeStatusRequestDto dto, HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String privateKey = jwtUtil.getPrivateKey(token);

        String tokenUri = nftServices.getTokenUri(privateKey, dto.getTokenId());

        String newUri = bookServices.updateBookMetadataOnIpfs(tokenUri, dto.getBook());

        nftServices.updateUri(privateKey, dto.getTokenId(), newUri);

        if(dto.getBook().isTrade()){
            if(bookRepository.existsById(dto.getTokenId())){
                Book book = bookRepository.findByNftId(dto.getTokenId());

                book.setIsbn(dto.getBook().getIsbn());
                book.setUri(newUri);

            }
            else{

                bookRepository.save(new Book(dto.getTokenId(), dto.getBook().getTitle(), dto.getBook().getAuthor(), dto.getBook().getPrice(), dto.getBook().getImg().get(0),
                        newUri, dto.getBook().getIsbn()));

                if(!nftServices.listedNft(privateKey, dto.getTokenId()))
                    nftServices.listBook(privateKey, dto.getTokenId(), BigInteger.valueOf(dto.getBook().getPrice()));

            }

        }
        else{
            if(bookRepository.existsById(dto.getTokenId()))
                bookRepository.delete(bookRepository.findByNftId(dto.getTokenId()));
            if(nftServices.listedNft(privateKey, dto.getTokenId()))
                nftServices.unListBook(privateKey, dto.getTokenId());
        }

        return true;
    }

    @GetMapping("/mine")
    public List<ResponseBookData> myBook(HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String privateKey = jwtUtil.getPrivateKey(token);

        List<TokenData> tokens = nftServices.getMyTokens(privateKey);

        List<ResponseBookData> books = bookServices.searchToTokenIds(privateKey, tokens);

        return books;
    }


    @GetMapping("/{isbn}")
    public List<Book> searchBook(@PathVariable String isbn) throws Exception {
        List<Book> books = bookRepository.findBooksByIsbn(isbn);


        return books;
    }
}
