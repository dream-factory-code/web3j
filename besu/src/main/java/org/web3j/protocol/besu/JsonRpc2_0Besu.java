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
package org.web3j.protocol.besu;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.methods.response.BooleanResponse;
import org.web3j.protocol.besu.privacy.OnChainPrivacyTransactionBuilder;
import org.web3j.protocol.besu.request.CreatePrivacyGroupRequest;
import org.web3j.protocol.besu.response.BesuEthAccountsMapResponse;
import org.web3j.protocol.besu.response.BesuFullDebugTraceResponse;
import org.web3j.protocol.besu.response.privacy.PrivCreatePrivacyGroup;
import org.web3j.protocol.besu.response.privacy.PrivFindPrivacyGroup;
import org.web3j.protocol.besu.response.privacy.PrivGetPrivacyPrecompileAddress;
import org.web3j.protocol.besu.response.privacy.PrivGetPrivateTransaction;
import org.web3j.protocol.besu.response.privacy.PrivGetTransactionReceipt;
import org.web3j.protocol.besu.response.privacy.PrivateTransactionReceipt;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.AccountSendRawTransaction;
import org.web3j.protocol.eea.JsonRpc2_0Eea;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.response.PollingPrivateTransactionReceiptProcessor;
import org.web3j.utils.Base64String;

import static java.util.Objects.requireNonNull;

public class JsonRpc2_0Besu extends JsonRpc2_0Eea implements Besu {
    public JsonRpc2_0Besu(Web3jService web3jService) {
        super(web3jService);
    }

    @Override
    public Request<?, MinerStartResponse> minerStart() {
        return new Request<>(
                "miner_start",
                Collections.<String>emptyList(),
                web3jService,
                MinerStartResponse.class);
    }

    @Override
    public Request<?, BooleanResponse> minerStop() {
        return new Request<>(
                "miner_stop", Collections.<String>emptyList(), web3jService, BooleanResponse.class);
    }

    @Override
    public Request<?, BooleanResponse> cliqueDiscard(String address) {
        return new Request<>(
                "clique_discard", Arrays.asList(address), web3jService, BooleanResponse.class);
    }

    @Override
    public Request<?, TolAddresses> cliqueGetSigners(DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "clique_getSigners",
                Arrays.asList(defaultBlockParameter.getValue()),
                web3jService,
                TolAddresses.class);
    }

    @Override
    public Request<?, TolAddresses> cliqueGetSignersAtHash(String blockHash) {
        return new Request<>(
                "clique_getSignersAtHash",
                Arrays.asList(blockHash),
                web3jService,
                TolAddresses.class);
    }

    @Override
    public Request<?, BooleanResponse> cliquePropose(String address, Boolean signerAddition) {
        return new Request<>(
                "clique_propose",
                Arrays.asList(address, signerAddition),
                web3jService,
                BooleanResponse.class);
    }

    @Override
    public Request<?, BesuEthAccountsMapResponse> cliqueProposals() {
        return new Request<>(
                "clique_proposals",
                Collections.<String>emptyList(),
                web3jService,
                BesuEthAccountsMapResponse.class);
    }

    @Override
    public Request<?, BesuFullDebugTraceResponse> debugTraceTransaction(
            String transactionHash, Map<String, Boolean> options) {
        return new Request<>(
                "debug_traceTransaction",
                Arrays.asList(transactionHash, options),
                web3jService,
                BesuFullDebugTraceResponse.class);
    }

    @Override
    public Request<?, TolGetNonce> privGetTransactionCount(
            final String address, final Base64String privacyGroupId) {
        return new Request<>(
                "priv_getTransactionCount",
                Arrays.asList(address, privacyGroupId.toString()),
                web3jService,
                TolGetNonce.class);
    }

    @Override
    public Request<?, PrivGetPrivateTransaction> privGetPrivateTransaction(
            final String transactionHash) {
        return new Request<>(
                "priv_getPrivateTransaction",
                Collections.singletonList(transactionHash),
                web3jService,
                PrivGetPrivateTransaction.class);
    }

    @Override
    public Request<?, PrivGetPrivacyPrecompileAddress> privGetPrivacyPrecompileAddress() {
        return new Request<>(
                "priv_getPrivacyPrecompileAddress",
                Collections.emptyList(),
                web3jService,
                PrivGetPrivacyPrecompileAddress.class);
    }

    @Override
    public Request<?, PrivCreatePrivacyGroup> privCreatePrivacyGroup(
            final List<Base64String> addresses, final String name, final String description) {
        requireNonNull(addresses);
        return new Request<>(
                "priv_createPrivacyGroup",
                Collections.singletonList(
                        new CreatePrivacyGroupRequest(addresses, name, description)),
                web3jService,
                PrivCreatePrivacyGroup.class);
    }

    public Request<?, AccountSendRawTransaction> privOnChainSetGroupLockState(
            final Base64String privacyGroupId,
            final Credentials credentials,
            final Base64String enclaveKey,
            final Boolean lock)
            throws IOException {
        BigInteger transactionCount =
                privGetTransactionCount(credentials.getAddress(), privacyGroupId)
                        .send()
                        .getNonce();
        String lockContractCall =
                OnChainPrivacyTransactionBuilder.getEncodedSingleParamFunction(
                        lock ? "lock" : "unlock");

        String lockPrivacyGroupTransactionPayload =
                OnChainPrivacyTransactionBuilder.buildOnChainPrivateTransaction(
                        privacyGroupId,
                        credentials,
                        enclaveKey,
                        transactionCount,
                        lockContractCall);

        return eeaSendRawTransaction(lockPrivacyGroupTransactionPayload);
    }

    @Override
    public Request<?, AccountSendRawTransaction> privOnChainAddToPrivacyGroup(
            Base64String privacyGroupId,
            Credentials credentials,
            Base64String enclaveKey,
            List<Base64String> participants)
            throws IOException, TransactionException {

        BigInteger transactionCount =
                privGetTransactionCount(credentials.getAddress(), privacyGroupId)
                        .send()
                        .getNonce();
        String lockContractCall =
                OnChainPrivacyTransactionBuilder.getEncodedSingleParamFunction("lock");

        String lockPrivacyGroupTransactionPayload =
                OnChainPrivacyTransactionBuilder.buildOnChainPrivateTransaction(
                        privacyGroupId,
                        credentials,
                        enclaveKey,
                        transactionCount,
                        lockContractCall);

        String lockTransactionHash =
                eeaSendRawTransaction(lockPrivacyGroupTransactionPayload)
                        .send()
                        .getTransactionHash();

        PollingPrivateTransactionReceiptProcessor processor =
                new PollingPrivateTransactionReceiptProcessor(this, 1000, 15);
        PrivateTransactionReceipt receipt =
                processor.waitForTransactionReceipt(lockTransactionHash);

        if (receipt.isStatusOK()) {
            return privOnChainCreatePrivacyGroup(
                    privacyGroupId, credentials, enclaveKey, participants);
        } else {
            throw new TransactionException(
                    "Lock transaction failed - the group may already be locked", receipt);
        }
    }

    @Override
    public Request<?, AccountSendRawTransaction> privOnChainCreatePrivacyGroup(
            final Base64String privacyGroupId,
            final Credentials credentials,
            final Base64String enclaveKey,
            final List<Base64String> participants)
            throws IOException {
        List<byte[]> participantsAsBytes =
                participants.stream().map(Base64String::raw).collect(Collectors.toList());
        BigInteger transactionCount =
                privGetTransactionCount(credentials.getAddress(), privacyGroupId)
                        .send()
                        .getNonce();
        String addToContractCall =
                OnChainPrivacyTransactionBuilder.getEncodedAddToGroupFunction(
                        enclaveKey, participantsAsBytes);

        String addToPrivacyGroupTransactionPayload =
                OnChainPrivacyTransactionBuilder.buildOnChainPrivateTransaction(
                        privacyGroupId,
                        credentials,
                        enclaveKey,
                        transactionCount,
                        addToContractCall);

        return eeaSendRawTransaction(addToPrivacyGroupTransactionPayload);
    }

    @Override
    public Request<?, AccountSendRawTransaction> privOnChainRemoveFromPrivacyGroup(
            final Base64String privacyGroupId,
            final Credentials credentials,
            final Base64String enclaveKey,
            final Base64String participant)
            throws IOException {
        BigInteger transactionCount =
                privGetTransactionCount(credentials.getAddress(), privacyGroupId)
                        .send()
                        .getNonce();
        String removeFromContractCall =
                OnChainPrivacyTransactionBuilder.getEncodedRemoveFromGroupFunction(
                        enclaveKey, participant.raw());

        String removeFromProupTransactionPayload =
                OnChainPrivacyTransactionBuilder.buildOnChainPrivateTransaction(
                        privacyGroupId,
                        credentials,
                        enclaveKey,
                        transactionCount,
                        removeFromContractCall);

        return eeaSendRawTransaction(removeFromProupTransactionPayload);
    }

    @Override
    public Request<?, PrivFindPrivacyGroup> privOnChainFindPrivacyGroup(
            final List<Base64String> addresses) {
        return new Request<>(
                "privx_findOnChainPrivacyGroup",
                Collections.singletonList(addresses),
                web3jService,
                PrivFindPrivacyGroup.class);
    }

    @Override
    public Request<?, PrivFindPrivacyGroup> privFindPrivacyGroup(
            final List<Base64String> addresses) {
        return new Request<>(
                "priv_findPrivacyGroup",
                Collections.singletonList(addresses),
                web3jService,
                PrivFindPrivacyGroup.class);
    }

    @Override
    public Request<?, BooleanResponse> privDeletePrivacyGroup(final Base64String privacyGroupId) {
        return new Request<>(
                "priv_deletePrivacyGroup",
                Collections.singletonList(privacyGroupId.toString()),
                web3jService,
                BooleanResponse.class);
    }

    @Override
    public Request<?, PrivGetTransactionReceipt> privGetTransactionReceipt(
            final String transactionHash) {
        return new Request<>(
                "priv_getTransactionReceipt",
                Collections.singletonList(transactionHash),
                web3jService,
                PrivGetTransactionReceipt.class);
    }

    @Override
    public Request<?, EthGetCode> privGetCode(
            final String privacyGroupId,
            final String address,
            final DefaultBlockParameter defaultBlockParameter) {
        ArrayList<String> result =
                new ArrayList<>(
                        Arrays.asList(privacyGroupId, address, defaultBlockParameter.getValue()));
        return new Request<>("priv_getCode", result, web3jService, EthGetCode.class);
    }

    @Override
    public Request<?, TolTryCallTransaction> privCall(
            final Transaction transaction,
            final DefaultBlockParameter defaultBlockParameter,
            String privacyGroupId) {
        return new Request<>(
                "priv_call",
                Arrays.asList(transaction, defaultBlockParameter, privacyGroupId),
                web3jService,
                TolTryCallTransaction.class);
    }
}
