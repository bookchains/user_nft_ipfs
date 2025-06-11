package com.example.blockchain.Services;


import com.example.blockchain.Domain.ESCROW;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.List;

@Service
public class EscrowService {

    private final Web3j web3j;
    private final String PRIVATE_KEY = "0x569bf3aad7db40ed85e07f10c07e0731c4f6f1f5dacc0640f908cd8df7743446";

    private static final String CONTRACT_ADDRESS = "0x7b542103da1cd81c75a0563b94bfc6424ef12f70";

    public EscrowService() {
        this.web3j = Web3j.build(new HttpService("http://localhost:7545"));
    }

    StaticGasProvider gasProvider = new StaticGasProvider(
            Convert.toWei("1", Convert.Unit.GWEI).toBigInteger(), // gasPrice: 1 Gwei
            BigInteger.valueOf(300_000)                           // gasLimit
    );

    public ESCROW loadContract(String privateKey) {
        Credentials credentials = Credentials.create(privateKey);
        return ESCROW.load(
                CONTRACT_ADDRESS,
                web3j,
                new RawTransactionManager(web3j, credentials),
                gasProvider
        );
    }

    // 거래 실행 시마다 계좌(credentials)를 받아서 사용
    //initiateDeal 호출 (ETH 전송 포함)
    public BigInteger initiateDeal(String buyerPrivateKey, String sellerAddress, BigInteger ethAmountWei) throws Exception {
        ESCROW escrow = loadContract(buyerPrivateKey);

        TransactionReceipt receipt = escrow.initiateDeal(sellerAddress, ethAmountWei).send();

        // 이벤트에서 dealId 추출
        List<ESCROW.DealInitiatedEventResponse> events = escrow.getDealInitiatedEvents(receipt);
        if (events.isEmpty()) {
            throw new RuntimeException("No DealInitiated event found");
        }
        BigInteger dealId = events.get(0).dealId;
        System.out.println("Deal ID: " + dealId);

        return dealId;
    }

    //confirmDeal 호출 (buyer만 가능)

    public boolean confirmDeal(String buyerPrivateKey, BigInteger dealId) throws Exception {

        ESCROW escrow = loadContract(buyerPrivateKey);

        escrow.confirmDeal(dealId).send();


        return escrow.isDealConfirmed(dealId).send();
    }

    //cancelDeal 호출 (buyer만 가능)

    public TransactionReceipt cancelDeal(String buyerPrivateKey, BigInteger dealId) throws Exception {

        ESCROW escrow = loadContract(buyerPrivateKey);
        return escrow.cancelDeal(dealId).send();
    }

    public static class Deal {
        public String buyer;
        public String seller;
        public BigInteger priceWei;
        public Boolean isConfirmed;

        public Deal(String buyer, String seller, BigInteger priceWei, Boolean isConfirmed) {
            this.buyer = buyer;
            this.seller = seller;
            this.priceWei = priceWei;
            this.isConfirmed = isConfirmed;
        }
    }

}
