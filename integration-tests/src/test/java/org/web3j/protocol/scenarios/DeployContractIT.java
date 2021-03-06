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
import java.util.List;

import org.junit.jupiter.api.Test;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.AccountSendRawTransaction;
import org.web3j.protocol.core.methods.response.TolTryCallTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Integration test demonstrating the full contract deployment workflow. */
public class DeployContractIT extends Scenario {

    @Test
    public void testContractCreation() throws Exception {
        boolean accountUnlocked = unlockAccount();
        assertTrue(accountUnlocked);

        String transactionHash = sendTransaction();
        assertFalse(transactionHash.isEmpty());

        TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHash);

        assertEquals(transactionReceipt.getHash(), (transactionHash));

        assertFalse(transactionReceipt.getGasUsed().equals(GAS_LIMIT));

        String contractAddress = transactionReceipt.getNewAddress();

        assertNotNull(contractAddress);

        Function function = createFibonacciFunction();

        String responseValue = callSmartContractFunction(function, contractAddress);
        assertFalse(responseValue.isEmpty());

        List<Type> uint =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        assertEquals(uint.size(), (1));
        assertEquals(uint.get(0).getValue(), (BigInteger.valueOf(13)));
    }

    private String sendTransaction() throws Exception {
        BigInteger nonce = getNonce(ALICE.getAddress());

        Transaction transaction =
                Transaction.createDeployContractTransaction(
                        ALICE.getAddress(),
                        nonce,
                        GAS_PRICE,
                        GAS_LIMIT,
                        BigInteger.ZERO,
                        getFibonacciSolidityBinary());

        AccountSendRawTransaction transactionResponse =
                web3j.accountSendRawTransaction(transaction).sendAsync().get();

        return transactionResponse.getTransactionHash();
    }

    private String callSmartContractFunction(Function function, String contractAddress)
            throws Exception {

        String encodedFunction = FunctionEncoder.encode(function);

        TolTryCallTransaction response =
                web3j.tolTryCallTransaction(
                                Transaction.createTryCallTransaction(
                                        ALICE.getAddress(), contractAddress, encodedFunction),
                                DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

        return response.getValue();
    }
}
