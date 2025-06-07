package com.example.blockchain.Domain;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class ESCROW extends Contract {
    public static final String BINARY = "60806040526000805534801561001457600080fd5b50610d7d806100246000396000f3fe60806040526004361061004a5760003560e01c806303988f841461004f57806331ea1a391461008f5780635cb442bb146100cc578063a3976cd414610109578063b37c250314610146575b600080fd5b34801561005b57600080fd5b5061007660048036038101906100719190610937565b610176565b60405161008694939291906109cf565b60405180910390f35b34801561009b57600080fd5b506100b660048036038101906100b19190610937565b6101f3565b6040516100c39190610a14565b60405180910390f35b3480156100d857600080fd5b506100f360048036038101906100ee9190610937565b610459565b6040516101009190610a14565b60405180910390f35b34801561011557600080fd5b50610130600480360381019061012b9190610937565b610674565b60405161013d9190610a14565b60405180910390f35b610160600480360381019061015b9190610a5b565b6106a1565b60405161016d9190610a88565b60405180910390f35b60016020528060005260406000206000915090508060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16908060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16908060020154908060030160009054906101000a900460ff16905084565b6000816001600082815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461029a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161029190610b00565b60405180910390fd5b6000600160008581526020019081526020016000206040518060800160405290816000820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020016001820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600282015481526020016003820160009054906101000a900460ff16151515158152505090508060600151156103d2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016103c990610b6c565b60405180910390fd5b806000015173ffffffffffffffffffffffffffffffffffffffff166108fc82604001519081150290604051600060405180830381858888f19350505050158015610420573d6000803e3d6000fd5b50837f31b0c01e8429f81903c3f8adc204767b8efa7cfca91a00f3b4bb40ce63c8a20660405160405180910390a2600192505050919050565b6000816001600082815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610500576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104f790610b00565b60405180910390fd5b6000600160008581526020019081526020016000209050600081600201541161055e576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161055590610bd8565b60405180910390fd5b8060030160009054906101000a900460ff16156105b0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016105a790610b6c565b60405180910390fd5b60018160030160006101000a81548160ff0219169083151502179055508060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc82600201549081150290604051600060405180830381858888f1935050505015801561063b573d6000803e3d6000fd5b50837fe70f0403e69c896bbcf61b8ca0224ab04d6de1ade440bc896049efe6fe4e073160405160405180910390a2600192505050919050565b60006001600083815260200190815260200160002060030160009054906101000a900460ff169050919050565b60008173ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1603610711576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161070890610c44565b60405180910390fd5b60003411610754576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161074b90610cb0565b60405180910390fd5b60008081548092919061076690610cff565b919050555060405180608001604052803373ffffffffffffffffffffffffffffffffffffffff1681526020018373ffffffffffffffffffffffffffffffffffffffff16815260200134815260200160001515815250600160008054815260200190815260200160002060008201518160000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506040820151816002015560608201518160030160006101000a81548160ff0219169083151502179055509050508173ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff166000547ff5be5a8540072a013ed532a20cacd37587fdacad63467da756f4d7a2d2103c0f346040516108ea9190610a88565b60405180910390a46000549050919050565b600080fd5b6000819050919050565b61091481610901565b811461091f57600080fd5b50565b6000813590506109318161090b565b92915050565b60006020828403121561094d5761094c6108fc565b5b600061095b84828501610922565b91505092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b600061098f82610964565b9050919050565b61099f81610984565b82525050565b6109ae81610901565b82525050565b60008115159050919050565b6109c9816109b4565b82525050565b60006080820190506109e46000830187610996565b6109f16020830186610996565b6109fe60408301856109a5565b610a0b60608301846109c0565b95945050505050565b6000602082019050610a2960008301846109c0565b92915050565b610a3881610984565b8114610a4357600080fd5b50565b600081359050610a5581610a2f565b92915050565b600060208284031215610a7157610a706108fc565b5b6000610a7f84828501610a46565b91505092915050565b6000602082019050610a9d60008301846109a5565b92915050565b600082825260208201905092915050565b7f4e6f742062757965720000000000000000000000000000000000000000000000600082015250565b6000610aea600983610aa3565b9150610af582610ab4565b602082019050919050565b60006020820190508181036000830152610b1981610add565b9050919050565b7f416c726561647920636f6e6669726d6564000000000000000000000000000000600082015250565b6000610b56601183610aa3565b9150610b6182610b20565b602082019050919050565b60006020820190508181036000830152610b8581610b49565b9050919050565b7f4e6f206465616c00000000000000000000000000000000000000000000000000600082015250565b6000610bc2600783610aa3565b9150610bcd82610b8c565b602082019050919050565b60006020820190508181036000830152610bf181610bb5565b9050919050565b7f42757965722063616e6e6f742062652073656c6c657200000000000000000000600082015250565b6000610c2e601683610aa3565b9150610c3982610bf8565b602082019050919050565b60006020820190508181036000830152610c5d81610c21565b9050919050565b7f4d7573742073656e642045544800000000000000000000000000000000000000600082015250565b6000610c9a600d83610aa3565b9150610ca582610c64565b602082019050919050565b60006020820190508181036000830152610cc981610c8d565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000610d0a82610901565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8203610d3c57610d3b610cd0565b5b60018201905091905056fea264697066735822122085b66e3e56cbf6a37ffad3f4d904b11d2fc2436188e3964c9c01239da922f82b64736f6c63430008110033";

    private static String librariesLinkedBinary;

    public static final String FUNC_CANCELDEAL = "cancelDeal";

    public static final String FUNC_CONFIRMDEAL = "confirmDeal";

    public static final String FUNC_DEALS = "deals";

    public static final String FUNC_INITIATEDEAL = "initiateDeal";

    public static final String FUNC_ISDEALCONFIRMED = "isDealConfirmed";

    public static final Event DEALCANCELLED_EVENT = new Event("DealCancelled", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    public static final Event DEALCOMPLETED_EVENT = new Event("DealCompleted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    public static final Event DEALINITIATED_EVENT = new Event("DealInitiated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected ESCROW(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ESCROW(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ESCROW(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ESCROW(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<DealCancelledEventResponse> getDealCancelledEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DEALCANCELLED_EVENT, transactionReceipt);
        ArrayList<DealCancelledEventResponse> responses = new ArrayList<DealCancelledEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DealCancelledEventResponse typedResponse = new DealCancelledEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.dealId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DealCancelledEventResponse getDealCancelledEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DEALCANCELLED_EVENT, log);
        DealCancelledEventResponse typedResponse = new DealCancelledEventResponse();
        typedResponse.log = log;
        typedResponse.dealId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<DealCancelledEventResponse> dealCancelledEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDealCancelledEventFromLog(log));
    }

    public Flowable<DealCancelledEventResponse> dealCancelledEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEALCANCELLED_EVENT));
        return dealCancelledEventFlowable(filter);
    }

    public static List<DealCompletedEventResponse> getDealCompletedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DEALCOMPLETED_EVENT, transactionReceipt);
        ArrayList<DealCompletedEventResponse> responses = new ArrayList<DealCompletedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DealCompletedEventResponse typedResponse = new DealCompletedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.dealId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DealCompletedEventResponse getDealCompletedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DEALCOMPLETED_EVENT, log);
        DealCompletedEventResponse typedResponse = new DealCompletedEventResponse();
        typedResponse.log = log;
        typedResponse.dealId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<DealCompletedEventResponse> dealCompletedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDealCompletedEventFromLog(log));
    }

    public Flowable<DealCompletedEventResponse> dealCompletedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEALCOMPLETED_EVENT));
        return dealCompletedEventFlowable(filter);
    }

    public static List<DealInitiatedEventResponse> getDealInitiatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DEALINITIATED_EVENT, transactionReceipt);
        ArrayList<DealInitiatedEventResponse> responses = new ArrayList<DealInitiatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DealInitiatedEventResponse typedResponse = new DealInitiatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.dealId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.seller = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DealInitiatedEventResponse getDealInitiatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DEALINITIATED_EVENT, log);
        DealInitiatedEventResponse typedResponse = new DealInitiatedEventResponse();
        typedResponse.log = log;
        typedResponse.dealId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.buyer = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.seller = (String) eventValues.getIndexedValues().get(2).getValue();
        typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<DealInitiatedEventResponse> dealInitiatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDealInitiatedEventFromLog(log));
    }

    public Flowable<DealInitiatedEventResponse> dealInitiatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEALINITIATED_EVENT));
        return dealInitiatedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> cancelDeal(BigInteger dealId) {
        final Function function = new Function(
                FUNC_CANCELDEAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(dealId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> confirmDeal(BigInteger dealId) {
        final Function function = new Function(
                FUNC_CONFIRMDEAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(dealId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple4<String, String, BigInteger, Boolean>> deals(
            BigInteger param0) {
        final Function function = new Function(FUNC_DEALS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple4<String, String, BigInteger, Boolean>>(function,
                new Callable<Tuple4<String, String, BigInteger, Boolean>>() {
                    @Override
                    public Tuple4<String, String, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, String, BigInteger, Boolean>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> initiateDeal(String seller, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_INITIATEDEAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, seller)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<Boolean> isDealConfirmed(BigInteger dealId) {
        final Function function = new Function(FUNC_ISDEALCONFIRMED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(dealId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static ESCROW load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new ESCROW(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ESCROW load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ESCROW(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ESCROW load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new ESCROW(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ESCROW load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ESCROW(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ESCROW> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ESCROW.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<ESCROW> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ESCROW.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static RemoteCall<ESCROW> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ESCROW.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<ESCROW> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ESCROW.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static void linkLibraries(List<Contract.LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class DealCancelledEventResponse extends BaseEventResponse {
        public BigInteger dealId;
    }

    public static class DealCompletedEventResponse extends BaseEventResponse {
        public BigInteger dealId;
    }

    public static class DealInitiatedEventResponse extends BaseEventResponse {
        public BigInteger dealId;

        public String buyer;

        public String seller;

        public BigInteger price;
    }
}
