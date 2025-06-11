package com.example.blockchain.Services;

import com.example.blockchain.Domain.Dto.BookMetaData;
import com.example.blockchain.Domain.Dto.ResponseBookData;
import com.example.blockchain.Domain.Dto.TokenData;
import com.example.blockchain.Repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class BookServices {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private NFTServices nftServices;


    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:5001/api/v0") // 로컬 IPFS API 주소
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String uploadBookMetadata(BookMetaData book) throws Exception {
        String json = objectMapper.writeValueAsString(book);

        // multipart/form-data 형식으로 POST
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new ByteArrayResource(json.getBytes()) {
            @Override
            public String getFilename() {
                return "book.json";
            }
        });

        // multipart/form-data 형식으로 POST
        Mono<String> responseMono = webClient.post()
                .uri("/add")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block();

        // 응답 JSON에서 "Hash" 값을 추출
        // 예: {"Name":"book.json","Hash":"QmXyz...","Size":"1234"}
        JsonNode node = objectMapper.readTree(response);
        String hash = node.get("Hash").asText();

        return "ipfs://" + hash;
    }

    private MultiValueMap<String, Object> createMultipartBody(String json) {
        var map = new LinkedMultiValueMap<String, Object>();

        var resource = new ByteArrayResource(json.getBytes()) {
            @Override
            public String getFilename() {
                return "book.json";
            }
        };
        map.add("file", resource);

        return map;
    }

    public BookMetaData fetchBookMetadataFromIpfs(String ipfsUri) throws Exception {
        // ipfs://QmXyz... → QmXyz...
        String hash = ipfsUri.replace("ipfs://", "");

        // 로컬 IPFS API를 통해 파일 내용 불러오기
        String json = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cat")
                        .queryParam("arg", hash)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return objectMapper.readValue(json, BookMetaData.class);
    }

    public String updateBookMetadataOnIpfs(String ipfsUri, BookMetaData newBook) throws Exception {
        // 기존 메타데이터 불러오기
        BookMetaData book = fetchBookMetadataFromIpfs(ipfsUri);

        // 외부에서 정의한 수정 로직을 적용
        if(book.isTrade()){
            book.setTrade(false);
        }
        else{
            book.setTrade(true);
            book.setPrice(newBook.getPrice());
            book.setTradeMethod(newBook.getTradeMethod());
            book.setPostPrice(newBook.getPostPrice());
            book.setTradePlace(newBook.getTradePlace());
            book.setDescription(newBook.getDescription());
        }
        // 수정된 데이터 다시 업로드
        return uploadBookMetadata(book);
    }

//    public List<ResponseBookData> homeBook(List<TokenData> allToken) throws Exception {
//        List<ResponseBookData> bookMetaDataList = new ArrayList<>();
//        for(TokenData token : allToken){
//
//
//            ResponseBookData book = new ResponseBookData(token.getTokenId() ,fetchBookMetadataFromIpfs(token.getUri()));
//
//            bookMetaDataList.add(book);
//
//        }
//        return bookMetaDataList;
//    }

    public List<ResponseBookData> searchToTokenIds(String key, List<TokenData> Tokens) throws Exception {
        List<ResponseBookData> bookList = new ArrayList<>();
        for(TokenData i : Tokens){
            BookMetaData book = fetchBookMetadataFromIpfs(i.getUri());

            String seller = nftServices.OwnerOf(key, i.getTokenId());

            bookList.add(new ResponseBookData(i.getTokenId(), book, seller));

        }
        return bookList;
    }


    public boolean checkISBN(String isbn){
        String url = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/search/book_adv.json")
                .queryParam("d_isbn", isbn)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                int total = root.path("total").asInt();
                return total > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }
}
