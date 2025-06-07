package com.example.blockchain.Controller;

import com.example.blockchain.Repository.BookRepository;
import com.example.blockchain.jwt.JwtProperties;
import com.example.blockchain.jwt.JwtUtil;
import com.example.blockchain.Domain.Booknft;
import com.example.blockchain.Domain.Dto.*;
import com.example.blockchain.Services.BookServices;
import com.example.blockchain.Services.EscrowService;
import com.example.blockchain.Services.NFTServices;
import com.example.blockchain.Status.StatusCode;
import com.example.blockchain.jwt.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/nft")
@RequiredArgsConstructor
public class NFTController {

    @Autowired
    private NFTServices nftServices;
    @Autowired
    private BookServices bookServices;
    @Autowired
    private EscrowService escrowService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/{symbol}")
    public ResponseEntity<ResponseBookData> getBook(@RequestBody RegisterBookRequestDto dto, HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String privateKey = jwtUtil.getPrivateKey(token);

        String uri = nftServices.getTokenUri( privateKey, dto.getNftId());

        BookMetaData ipfsBook = bookServices.fetchBookMetadataFromIpfs(uri);

        ResponseBookData book = new ResponseBookData(dto.getNftId(), ipfsBook);

        return ResponseEntity.ok(book);
    }

    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<Deal>> tradeHistory(@PathVariable BigInteger symbol) throws Exception {
        List<Deal> history =  nftServices.getTradeHistoryByTokenId(symbol);

        return ResponseEntity.ok(history);
    }

    @PostMapping("/transfer-requset")
    public ResponseEntity<BigInteger> transfer(@RequestBody transferRequsetDto dto, HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String privateKey = jwtUtil.getPrivateKey(token);

        String seller = nftServices.OwnerOf(privateKey, dto.getTokenId());


        BigInteger tokenId = escrowService.initiateDeal(privateKey, seller, dto.getPrice());
        return  ResponseEntity.ok(tokenId);
    }

    @PostMapping("/confirm-transfer")
    public ResponseEntity<Boolean> transfer(@RequestBody SaveHistoryDto dto, HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String privateKey = jwtUtil.getPrivateKey(token);

        try {
            if(escrowService.confirmDeal(privateKey, dto.getDealId())){
                nftServices.transferBook(privateKey, dto.getTokenId());
                bookRepository.delete(bookRepository.findByNftId(dto.getTokenId()));
                return ResponseEntity.ok(true);
            }
        }catch (Exception e){
            e.notify();
        }
        return ResponseEntity
                .status(StatusCode.NOT_FOUND_DEAL)
                .body(false);
    }

    @PostMapping("/cancel-transfer")
    public ResponseEntity<Boolean> cancelDeal(@RequestBody CancelDealDto dto, HttpServletRequest request) throws Exception {
        String token = jwtUtil.resolveToken(request);
        String privateKey = jwtUtil.getPrivateKey(token);

        try {
            if(escrowService.confirmDeal(privateKey, dto.getDealId())) {
                return ResponseEntity.ok(true);
            }
        }catch (Exception e){
            e.notify();
        }
        return ResponseEntity
                .status(StatusCode.NOT_FOUND_DEAL)
                .body(false);
    }


}
