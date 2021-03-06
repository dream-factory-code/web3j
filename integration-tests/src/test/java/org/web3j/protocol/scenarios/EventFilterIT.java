/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.protocol.scenarios;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** Filter scenario integration tests. */
public class EventFilterIT extends Scenario {

    // Deployed Fibonacci contract instance in testnet
    private static final String CONTRACT_ADDRESS = "0x3c05b2564139fb55820b18b72e94b2178eaace7d";

    @Test
    public void testEventFilter() throws Exception {
        unlockAccount();

        Function function = createFibonacciFunction();
        String encodedFunction = FunctionEncoder.encode(function);

        BigInteger gas = estimateGas(encodedFunction);
        String transactionHash = sendTransaction(ALICE, CONTRACT_ADDRESS, gas, encodedFunction);

        TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHash);

        assertFalse(gas.equals(transactionReceipt.getGasUsed()));

        List<Log> logs = transactionReceipt.getLogs();
        assertFalse(logs.isEmpty());

        Log log = logs.get(0);

        List<String> topics = log.getTopics();
        assertEquals(topics.size(), (1));

        Event event =
                new Event(
                        "Notify",
                        Arrays.asList(
                                new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));

        // check function signature - we only have a single topic our event signature,
        // there are no indexed parameters in this example
        String encodedEventSignature = EventEncoder.encode(event);
        assertEquals(topics.get(0), (encodedEventSignature));

        // verify our two event parameters
        List<Type> results =
                FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());
        assertEquals(
                results,
                (Arrays.asList(
                        new Uint256(BigInteger.valueOf(7)), new Uint256(BigInteger.valueOf(13)))));

        // finally check it shows up in the event filter
        List<EthLog.LogResult> filterLogs =
                createFilterForEvent(encodedEventSignature, CONTRACT_ADDRESS);
        assertFalse(filterLogs.isEmpty());
    }

    private BigInteger estimateGas(String encodedFunction) throws Exception {
        TolGetGasEstimate tolGetGasEstimate =
                web3j.tolGetGasEstimate(
                                Transaction.createTryCallTransaction(
                                        ALICE.getAddress(), null, encodedFunction))
                        .sendAsync()
                        .get();
        // this was coming back as 50,000,000 which is > the block gas limit of 4,712,388
        // see eth.getBlock("latest")
        return tolGetGasEstimate.getGasEstimate().divide(BigInteger.valueOf(100));
    }

    private String sendTransaction(
            Credentials credentials, String contractAddress, BigInteger gas, String encodedFunction)
            throws Exception {
        BigInteger nonce = getNonce(credentials.getAddress());
        Transaction transaction =
                Transaction.createExecuteFunctionTransaction(
                        credentials.getAddress(),
                        nonce,
                        Transaction.DEFAULT_GAS,
                        gas,
                        contractAddress,
                        encodedFunction);

        AccountSendRawTransaction transactionResponse =
                web3j.accountSendRawTransaction(transaction).sendAsync().get();

        assertFalse(transactionResponse.hasError());

        return transactionResponse.getTransactionHash();
    }

    private List<EthLog.LogResult> createFilterForEvent(
            String encodedEventSignature, String contractAddress) throws Exception {
        EthFilter ethFilter =
                new EthFilter(
                        DefaultBlockParameterName.EARLIEST,
                        DefaultBlockParameterName.LATEST,
                        contractAddress);

        ethFilter.addSingleTopic(encodedEventSignature);

        EthLog ethLog = web3j.ethGetLogs(ethFilter).send();
        return ethLog.getLogs();
    }
}
