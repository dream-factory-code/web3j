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
package org.web3j.tx;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

/** Class for performing Ether transactions on the Tolar blockchain. */
public class Transfer extends ManagedTransaction {

    // This is the cost to send Ether between parties
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(21000);

    public Transfer(Web3j web3j, TransactionManager transactionManager) {
        super(web3j, transactionManager);
    }

    /**
     * Given the duration required to execute a transaction, asyncronous execution is strongly
     * recommended via {@link Transfer#(String, BigDecimal, Convert.Unit)}.
     *
     * @param toAddress destination address
     * @param value amount to send
     * @param unit of specified send
     * @return {@link Optional} containing our transaction receipt
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TransactionException if the transaction was not mined while waiting
     */
    private TransactionReceipt send(
            String toAddress,
            BigDecimal value,
            Convert.Unit unit,
            String senderAddressPassword,
            BigInteger nonce)
            throws IOException, InterruptedException, TransactionException {

        BigInteger gasPrice = requestCurrentGasPrice();
        return send(toAddress, value, unit, senderAddressPassword, GAS_LIMIT, gasPrice, nonce);
    }

    private TransactionReceipt send(
            String toAddress,
            BigDecimal value,
            Convert.Unit unit,
            String senderAddressPassword,
            BigInteger gas,
            BigInteger gasPrice,
            BigInteger nonce)
            throws IOException, InterruptedException, TransactionException {

        BigDecimal weiValue = Convert.toWei(value, unit);
        if (!Numeric.isIntegerValue(weiValue)) {
            throw new UnsupportedOperationException(
                    "Non decimal Wei value provided: "
                            + value
                            + " "
                            + unit.toString()
                            + " = "
                            + weiValue
                            + " Wei");
        }

        String resolvedAddress = ensResolver.resolve(toAddress);
        return send(resolvedAddress, weiValue.toBigIntegerExact(), gas, gasPrice, "");
    }

    public static RemoteCall<TransactionReceipt> sendFunds(
            Web3j web3j,
            Credentials credentials,
            String toAddress,
            BigDecimal value,
            Convert.Unit unit,
            String senderAddressPassword,
            BigInteger nonce)
            throws InterruptedException, IOException, TransactionException {

        TransactionManager transactionManager = new SignedTransactionManager(web3j, credentials);

        return new RemoteCall<>(
                () ->
                        new Transfer(web3j, transactionManager)
                                .send(toAddress, value, unit, senderAddressPassword, nonce));
    }

    /**
     * Execute the provided function as a transaction asynchronously. This is intended for one-off
     * fund transfers. For multiple, create an instance.
     *
     * @param toAddress destination address
     * @param value amount to send
     * @param unit of specified send
     * @return {@link RemoteCall} containing executing transaction
     */
    public RemoteCall<TransactionReceipt> sendFunds(
            String toAddress,
            BigDecimal value,
            Convert.Unit unit,
            String senderAddressPassword,
            BigInteger nonce) {
        return new RemoteCall<>(() -> send(toAddress, value, unit, senderAddressPassword, nonce));
    }

    public RemoteCall<TransactionReceipt> sendFunds(
            String toAddress,
            BigDecimal value,
            Convert.Unit unit,
            String senderAddressPassword,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger nonce) {
        return new RemoteCall<>(
                () ->
                        send(
                                toAddress,
                                value,
                                unit,
                                senderAddressPassword,
                                gasLimit,
                                gasPrice,
                                nonce));
    }
}
