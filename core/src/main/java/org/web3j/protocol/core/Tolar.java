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
package org.web3j.protocol.core;

import java.math.BigInteger;
import java.util.List;

import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.request.ShhFilter;
import org.web3j.protocol.core.methods.request.SignedTransaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.admin.AdminNodeInfo;
import org.web3j.protocol.core.methods.response.admin.AdminPeers;

/** Core Tolar JSON-RPC API. */
public interface Tolar {
    Request<?, Web3ClientVersion> web3ClientVersion();

    Request<?, Web3Sha3> web3Sha3(String data);

    Request<?, NetVersion> netVersion();

    Request<?, NetListening> netListening();

    Request<?, NetPeerCount> netPeerCount();

    Request<?, AdminNodeInfo> adminNodeInfo();

    Request<?, AdminPeers> adminPeers();

    Request<?, EthProtocolVersion> ethProtocolVersion();

    Request<?, EthChainId> ethChainId();

    Request<?, EthCoinbase> ethCoinbase();

    Request<?, EthSyncing> ethSyncing();

    Request<?, EthMining> ethMining();

    Request<?, EthHashrate> ethHashrate();

    Request<?, EthGasPrice> ethGasPrice();

    Request<?, TolAddresses> accountListAddresses();

    Request<?, TolGetBlockCount> tolGetBlockCount();

    Request<?, TolGetBalance> tolGetBalance(String address, BigInteger blockIndex);

    Request<?, EthGetStorageAt> ethGetStorageAt(
            String address, BigInteger position, DefaultBlockParameter defaultBlockParameter);

    Request<?, TolGetNonce> tolGetNonce(String address);

    Request<?, EthGetBlockTransactionCountByHash> ethGetBlockTransactionCountByHash(
            String blockHash);

    Request<?, EthGetBlockTransactionCountByNumber> ethGetBlockTransactionCountByNumber(
            DefaultBlockParameter defaultBlockParameter);

    Request<?, EthGetUncleCountByBlockHash> ethGetUncleCountByBlockHash(String blockHash);

    Request<?, EthGetUncleCountByBlockNumber> ethGetUncleCountByBlockNumber(
            DefaultBlockParameter defaultBlockParameter);

    Request<?, EthGetCode> ethGetCode(String address, DefaultBlockParameter defaultBlockParameter);

    Request<?, EthSign> ethSign(String address, String sha3HashOfDataToSign);

    Request<?, AccountSendRawTransaction> accountSendRawTransaction(
            org.web3j.protocol.core.methods.request.Transaction transaction);

    Request<?, AccountSendRawTransaction> ethSendRawTransaction(String signedTransactionData);

    Request<?, TolTryCallTransaction> tolTryCallTransaction(
            org.web3j.protocol.core.methods.request.Transaction transaction);

    Request<?, TolGetGasEstimate> tolGetGasEstimate(
            org.web3j.protocol.core.methods.request.Transaction transaction);

    Request<?, TolBlock> tolGetBlockByHash(String blockHash);

    Request<?, TolBlock> tolGetBlockByIndex(DefaultBlockParameter defaultBlockParameter);

    Request<?, TolTransaction> tolGetTransaction(String transactionHash);

    Request<?, TolTransaction> ethGetTransactionByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex);

    Request<?, TolTransaction> ethGetTransactionByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex);

    Request<?, TolGetTransactionReceipt> tolGetTransactionReceipt(String transactionHash);

    Request<?, TolBlock> ethGetUncleByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex);

    Request<?, TolBlock> ethGetUncleByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex);

    Request<?, EthGetCompilers> ethGetCompilers();

    Request<?, EthCompileLLL> ethCompileLLL(String sourceCode);

    Request<?, EthCompileSolidity> ethCompileSolidity(String sourceCode);

    Request<?, EthCompileSerpent> ethCompileSerpent(String sourceCode);

    Request<?, EthFilter> ethNewFilter(org.web3j.protocol.core.methods.request.EthFilter ethFilter);

    Request<?, EthFilter> ethNewBlockFilter();

    Request<?, EthFilter> ethNewPendingTransactionFilter();

    Request<?, EthUninstallFilter> ethUninstallFilter(BigInteger filterId);

    Request<?, EthLog> ethGetFilterChanges(BigInteger filterId);

    Request<?, EthLog> ethGetFilterLogs(BigInteger filterId);

    Request<?, EthLog> ethGetLogs(org.web3j.protocol.core.methods.request.EthFilter ethFilter);

    Request<?, EthGetWork> ethGetWork();

    Request<?, EthSubmitWork> ethSubmitWork(String nonce, String headerPowHash, String mixDigest);

    Request<?, EthSubmitHashrate> ethSubmitHashrate(String hashrate, String clientId);

    Request<?, DbPutString> dbPutString(String databaseName, String keyName, String stringToStore);

    Request<?, DbGetString> dbGetString(String databaseName, String keyName);

    Request<?, DbPutHex> dbPutHex(String databaseName, String keyName, String dataToStore);

    Request<?, DbGetHex> dbGetHex(String databaseName, String keyName);

    Request<?, org.web3j.protocol.core.methods.response.ShhPost> shhPost(
            org.web3j.protocol.core.methods.request.ShhPost shhPost);

    Request<?, ShhVersion> shhVersion();

    Request<?, ShhNewIdentity> shhNewIdentity();

    Request<?, ShhHasIdentity> shhHasIdentity(String identityAddress);

    Request<?, ShhNewGroup> shhNewGroup();

    Request<?, ShhAddToGroup> shhAddToGroup(String identityAddress);

    Request<?, ShhNewFilter> shhNewFilter(ShhFilter shhFilter);

    Request<?, ShhUninstallFilter> shhUninstallFilter(BigInteger filterId);

    Request<?, ShhMessages> shhGetFilterChanges(BigInteger filterId);

    Request<?, ShhMessages> shhGetMessages(BigInteger filterId);

    Request<?, IsMasterNode> netIsMasterNode();

    Request<?, MaxPeerCount> netMaxPeerCount();

    Request<?, MasterNodeCount> netMasterNodeCount();

    Request<?, TolGetTransactionList> tolGetTransactionList(
            List<String> addresses, long limit, long skip);

    Request<?, TolGetLatestBalance> tolGetLatestBalance(String address);

    Request<?, TolGetBlockchainInfo> tolGetBlockchainInfo();

    Request<?, AccountCreate> accountCreate(String masterPassword);

    Request<?, AccountOpen> accountOpen(String masterPassword);

    Request<?, AccountVerifyAddress> accountVerifyAddress(String address);

    Request<?, AccountCreateNewAddress> accountCreateNewAddress(
            String name, String lockPassword, String lockHint);

    Request<?, AccountExportKeyFile> accountExportKeyFile(String address);

    Request<?, AccountImportKeyFile> accountImportKeyFile(
            String keyFile, String name, String lockPassword, String lockHint);

    Request<?, AccountListBalancePerAddress> accountListBalancePerAddress();

    Request<?, AccountChangePassword> accountChangePassword(
            String oldMasterPassword, String newMasterPassword);

    Request<?, AccountChangeAddressPassword> accountChangeAddressPassword(
            String address, String oldPassword, String newPassword);

    Request<?, AccountSendRawTransaction> accountSendDeployContractTransaction(
            org.web3j.protocol.core.methods.request.Transaction transaction);

    Request<?, AccountSendRawTransaction> accountSendExecuteFunctionTransaction(
            org.web3j.protocol.core.methods.request.Transaction transaction);

    Request<?, AccountSendRawTransaction> accountSendFundTransferTransaction(
            org.web3j.protocol.core.methods.request.Transaction transaction);

    Request<?, AccountSendRawTransaction> txSendSignedTransaction(SignedTransaction transaction);

    Request<?, TolGetTransactionProtobuf> tolGetTransactionProtobuf(RawTransaction transaction);
}
