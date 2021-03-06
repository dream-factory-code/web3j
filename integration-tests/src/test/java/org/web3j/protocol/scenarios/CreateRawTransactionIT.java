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

import org.junit.jupiter.api.Test;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TolGetNonce;
import org.web3j.protocol.core.methods.response.AccountSendRawTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** Create, sign and send a raw transaction. */
public class CreateRawTransactionIT extends Scenario {

    @Test
    public void testTransferEther() throws Exception {
        BigInteger nonce = getNonce(ALICE.getAddress());
        RawTransaction rawTransaction = createEtherTransaction(nonce, BOB.getAddress());

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, ALICE);
        String hexValue = Numeric.toHexString(signedMessage);

        AccountSendRawTransaction accountSendRawTransaction =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = accountSendRawTransaction.getTransactionHash();

        assertFalse(transactionHash.isEmpty());

        TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHash);

        assertEquals(transactionHash, transactionReceipt.getHash());
    }

    @Test
    public void testDeploySmartContract() throws Exception {
        BigInteger nonce = getNonce(ALICE.getAddress());
        RawTransaction rawTransaction = createSmartContractTransaction(nonce);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, ALICE);
        String hexValue = Numeric.toHexString(signedMessage);

        AccountSendRawTransaction accountSendRawTransaction =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = accountSendRawTransaction.getTransactionHash();

        assertFalse(transactionHash.isEmpty());

        TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHash);

        assertEquals(transactionHash, transactionReceipt.getHash());

        assertFalse(
                rawTransaction.getGas().equals(transactionReceipt.getGasUsed()),
                "Contract execution ran out of gas");
    }

    private static RawTransaction createEtherTransaction(BigInteger nonce, String toAddress) {
        BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();

        return RawTransaction.createFundTransferTransaction(nonce, GAS_PRICE, GAS_LIMIT, toAddress, value);
    }

    private static RawTransaction createSmartContractTransaction(BigInteger nonce)
            throws Exception {
        return RawTransaction.createDeployContractTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, BigInteger.ZERO, getFibonacciSolidityBinary());
    }

    BigInteger getNonce(String address) throws Exception {
        TolGetNonce tolGetNonce =
                web3j.tolGetNonce(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

        return tolGetNonce.getNonce();
    }
}
