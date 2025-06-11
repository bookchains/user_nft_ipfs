package com.example.blockchain.Services;

import com.example.blockchain.Domain.Booknft;
import com.example.blockchain.Domain.Dto.Deal;
import com.example.blockchain.Domain.Dto.TokenData;
import com.example.blockchain.Domain.Entity.Book;
import com.example.blockchain.Repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.abi.datatypes.Type;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.web3j.tx.Contract.staticExtractEventParameters;

@AllArgsConstructor
@Service
public class NFTServices {

    @Autowired
    private BookRepository bookRepository;

    private final Web3j web3j;

    private final String PRIVATE_KEY = "0x569bf3aad7db40ed85e07f10c07e0731c4f6f1f5dacc0640f908cd8df7743446";
    private static final String CONTRACT_ADDRESS = "0xb055c4188251db062ac74b5ce32d87a85373129b";

    public NFTServices() throws Exception {
        this.web3j = Web3j.build(new HttpService("http://localhost:7545"));
    }

    StaticGasProvider gasProvider = new StaticGasProvider(
            Convert.toWei("1", Convert.Unit.GWEI).toBigInteger(), // gasPrice: 1 Gwei
            BigInteger.valueOf(300_000)                           // gasLimit
    );

    public Booknft loadContract(String privateKey) {
        Credentials credentials = Credentials.create(privateKey);
        return Booknft.load(
                CONTRACT_ADDRESS,
                web3j,
                new RawTransactionManager(web3j, credentials),
                gasProvider
        );
    }

    // 요청마다 사용자의 지갑을 기반으로 BookNFT 인스턴스 생성
    // NFT 민팅 (관리자만 가능)
    public BigInteger mintBook(String tokenURI, String to) throws Exception {
        Booknft contract = loadContract(PRIVATE_KEY);

        TransactionReceipt receipt =  contract.mintBook(to, tokenURI).send();

        // 이벤트에서 dealId 추출
        List<Booknft.NFTMintedEventResponse> events = contract.getNFTMintedEvents(receipt);
        if (events.isEmpty()) {
            throw new RuntimeException("No NFTMINTED event found");
        }
        BigInteger tokenId = events.get(0).tokenId;
        System.out.println("tokenId = " + tokenId);


        return tokenId;
    }

    // NFT 판매 등록
    public TransactionReceipt listBook(String privateKey, BigInteger tokenId, BigInteger priceWei) throws Exception {

        Booknft contract = loadContract(privateKey);

        TransactionReceipt approvalTx = contract.approve(CONTRACT_ADDRESS, tokenId).send();

        return contract.listItem(tokenId, priceWei).send();
    }

    // NFT 판매 등록 해제
    public TransactionReceipt unListBook(String privateKey, BigInteger tokenId) throws Exception {

        Booknft contract = loadContract(privateKey);


        return contract.unlistItem(tokenId).send();
    }

    // NFT 토큰 주인 계정 반환
    public String OwnerOf(String privateKey, BigInteger tokenId) throws Exception {
        Booknft contract = loadContract(privateKey);

        return contract.getOwnerOf(tokenId).send();
    }

    // NFT URI 반환
    public String getTokenUri(String privateKey, BigInteger tokenId) throws Exception {
        Booknft contract = loadContract(privateKey);

        return contract.getTokenURI(tokenId).send();
    }

    // NFT URI 업데이터
    public TransactionReceipt updateUri(String privateKey, BigInteger tokenId, String uri) throws Exception {
        Booknft contract = loadContract(privateKey);

        return contract.updateTokenURI(tokenId, uri).send();
    }

    // 내 소유 NFT 전체 조회(URI)
    public List<TokenData> getMyTokens(String privateKey) throws Exception{
        Booknft contract = loadContract(privateKey);

        Tuple2<List<BigInteger>, List<String>> result = contract.getMyTokenInfos().send();

        List<BigInteger> tokenIds = result.getValue1();
        List<String> uris = result.getValue2();

        List<TokenData> infoList = new ArrayList<>();
        for (int i = 0; i < tokenIds.size(); i++) {
            infoList.add(new TokenData(tokenIds.get(i), uris.get(i)));
        }

        return infoList;
    }

    // 전체 판매 중인 NFT 조회(URI)
    public List<TokenData> getSellingBookUris() throws Exception {
        Booknft contract = loadContract(PRIVATE_KEY);

        Tuple2<List<BigInteger>, List<String>> result = contract.getAllListedTokenInfos().send();

        List<BigInteger> tokenIds = result.getValue1();
        List<String> uris = result.getValue2();

        List<TokenData> infoList = new ArrayList<>();
        for (int i = 0; i < tokenIds.size(); i++) {
            infoList.add(new TokenData(tokenIds.get(i), uris.get(i)));
        }

        return infoList;
    }

    // 도서 거래 내역 반환
    public List<Deal> getTradeHistoryByTokenId(BigInteger tokenId) throws Exception {
        Booknft contract = loadContract(PRIVATE_KEY);

        List<TypeReference<?>> nonIndexedParams = new ArrayList<>();
        nonIndexedParams.add(new TypeReference<Uint128>() {});

        System.out.println("시작");

        Event event = new Event("BookPurchased",
                List.of(
                        new TypeReference<Uint256>(true) {},   // tokenId
                        new TypeReference<Address>(true) {},   // buyer
                        new TypeReference<Address>(true) {},  // seller
                        new TypeReference<Uint256>() {},       // price
                        new TypeReference<Utf8String>() {}     // ✅ purchaseDate는 non-indexed 여기에 포함
                )
        );

        String encodedEventSignature = EventEncoder.encode(event);

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(0)),
                DefaultBlockParameter.valueOf(web3j.ethBlockNumber().send().getBlockNumber()),
                CONTRACT_ADDRESS
        ).addSingleTopic(encodedEventSignature);

        List<Log> logs = web3j.ethGetLogs(filter).send().getLogs().stream()
                .map(logResult -> (Log) logResult.get())
                .toList();

        List<Booknft.BookPurchasedEventResponse> matchedEvents = new ArrayList<>();

        List<Deal> dealList = new ArrayList<>();

        for (Log log : logs) {
            EventValues eventValues = staticExtractEventParameters(event, log);
            Booknft.BookPurchasedEventResponse response = new Booknft.BookPurchasedEventResponse();

            response.log = log;
            response.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            response.buyer = (String) eventValues.getIndexedValues().get(1).getValue();
            response.seller = (String) eventValues.getIndexedValues().get(2).getValue();
            response.price = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            response.purchaseDate = (String) eventValues.getNonIndexedValues().get(1).getValue();

            if (response.tokenId.equals(tokenId)) {
                matchedEvents.add(response);
                dealList.add(new Deal(response.buyer, response.seller, response.price, response.purchaseDate));
            }
        }
        return dealList;
    }

    public TransactionReceipt transferBook(String privateKey, BigInteger tokenId) throws Exception {
        Booknft contract = loadContract(privateKey);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(new Date()); // 오늘 날짜

        return contract.purchaseItem(tokenId, formattedDate).send();
    }

    public BigDecimal getBalanceInEther(String address) throws Exception {
        BigInteger wei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance();

        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
    }

    public Boolean listedNft(String privateKey, BigInteger tokenId) throws Exception {
        Booknft contract = loadContract(privateKey);

        Tuple3<String, BigInteger, Boolean> send = contract.getListing(tokenId).send();


        return send.component3();
    }

}
