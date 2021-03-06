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

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.web3j.protocol.ResponseTester;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.TolGetNonce;
import org.web3j.protocol.core.methods.response.admin.AdminNodeInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Core Protocol Response tests. */
@Disabled("Not needed for HashNet")
public class ResponseTest extends ResponseTester {

    @Test
    public void testErrorResponse() {
        buildResponse(
                "{"
                        + "  \"jsonrpc\":\"2.0\","
                        + "  \"id\":1,"
                        + "  \"error\":{"
                        + "    \"code\":-32602,"
                        + "    \"message\":\"Invalid address length, expected 40 got 64 bytes\","
                        + "    \"data\":null"
                        + "  }"
                        + "}");

        TolBlock tolBlock = deserialiseResponse(TolBlock.class);
        assertTrue(tolBlock.hasError());
        assertEquals(
                tolBlock.getError(),
                (new Response.Error(-32602, "Invalid address length, expected 40 got 64 bytes")));
    }

    @Test
    public void testErrorResponseComplexData() {
        buildResponse(
                "{"
                        + "  \"jsonrpc\":\"2.0\","
                        + "  \"id\":1,"
                        + "  \"error\":{"
                        + "    \"code\":-32602,"
                        + "    \"message\":\"Invalid address length, expected 40 got 64 bytes\","
                        + "    \"data\":{\"foo\":\"bar\"}"
                        + "  }"
                        + "}");

        TolBlock tolBlock = deserialiseResponse(TolBlock.class);
        assertTrue(tolBlock.hasError());
        assertEquals(tolBlock.getError().getData(), ("{\"foo\":\"bar\"}"));
    }

    @Test
    public void testWeb3ClientVersion() {
        buildResponse(
                "{\n"
                        + "  \"id\":67,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": \"Mist/v0.9.3/darwin/go1.4.1\"\n"
                        + "}");

        Web3ClientVersion web3ClientVersion = deserialiseResponse(Web3ClientVersion.class);
        assertEquals(web3ClientVersion.getWeb3ClientVersion(), ("Mist/v0.9.3/darwin/go1.4.1"));
    }

    @Test
    public void testWeb3Sha3() throws IOException {
        buildResponse(
                "{\n"
                        + "  \"id\":64,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": "
                        + "\"0x47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad\"\n"
                        + "}");

        Web3Sha3 web3Sha3 = deserialiseResponse(Web3Sha3.class);
        assertEquals(
                web3Sha3.getResult(),
                ("0x47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"));
    }

    @Test
    public void testNetVersion() throws IOException {
        buildResponse(
                "{\n"
                        + "  \"id\":67,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"59\"\n"
                        + "}");

        NetVersion netVersion = deserialiseResponse(NetVersion.class);
        assertEquals(netVersion.getNetVersion(), ("59"));
    }

    @Test
    public void testNetListening() throws IOException {
        buildResponse(
                "{\n"
                        + "  \"id\":67,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\":true\n"
                        + "}");

        NetListening netListening = deserialiseResponse(NetListening.class);
        assertEquals(netListening.isListening(), (true));
    }

    @Test
    public void testNetPeerCount() throws IOException {
        buildResponse(
                "{\n"
                        + "  \"id\":74,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x2\"\n"
                        + "}");

        NetPeerCount netPeerCount = deserialiseResponse(NetPeerCount.class);
        assertEquals(netPeerCount.getQuantity(), (BigInteger.valueOf(2L)));
    }

    @Test
    public void testAdminNodeInfo() throws Exception {
        buildResponse(
                "{\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"id\": 1,\n"
                        + "    \"result\": {\n"
                        + "        \"id\": \"8ae75d6795f3541f897bcbfd3b4551aaf78b932cd0e91bf75a273940375c12a3\",\n"
                        + "        \"name\": \"Geth/v1.9.6-stable-bd059680/linux-amd64/go1.13.1\",\n"
                        + "        \"enode\": \"enode://1672a190f8c67669590db4b094c87573cbbc9b12f63d7137f505cfaa2cd2d35bea61abe1f8c898db4eab01d6c901270d7fff601b97a78f79ccefd83016b315cc@127.0.0.1:30303\",\n"
                        + "        \"enr\": \"enr:-Jq4QCKylmBZEJ1xizokiKyEST7FUrrOESva-sFWTkbBY6J0Xco6eUOkoc7lGOHy6yyCnjWhBEd35dr-c1FRxE3ozUEEg2V0aMrJhMs6ZLuDD8wlgmlkgnY0gmlwhH8AAAGJc2VjcDI1NmsxoQIWcqGQ-MZ2aVkNtLCUyHVzy7ybEvY9cTf1Bc-qLNLTW4N0Y3CCdl-DdWRwgnZf\",\n"
                        + "        \"ip\": \"127.0.0.1\",\n"
                        + "        \"ports\": {\n"
                        + "            \"discovery\": 30303,\n"
                        + "            \"listener\": 30303\n"
                        + "        },\n"
                        + "        \"listenAddr\": \"[::]:30303\",\n"
                        + "        \"protocols\": {\n"
                        + "            \"eth\": {\n"
                        + "                \"network\": 4,\n"
                        + "                \"difficulty\": 1,\n"
                        + "                \"genesis\": \"0x6341fd3daf94b748c72ced5a5b26028f2474f5f00d824504e4fa37a75767e177\",\n"
                        + "                \"config\": {\n"
                        + "                    \"chainId\": 4,\n"
                        + "                    \"homesteadBlock\": 1,\n"
                        + "                    \"daoForkSupport\": true,\n"
                        + "                    \"eip150Block\": 2,\n"
                        + "                    \"eip150Hash\": \"0x9b095b36c15eaf13044373aef8ee0bd3a382a5abb92e402afa44b8249c3a90e9\",\n"
                        + "                    \"eip155Block\": 3,\n"
                        + "                    \"eip158Block\": 3,\n"
                        + "                    \"byzantiumBlock\": 1035301,\n"
                        + "                    \"constantinopleBlock\": 3660663,\n"
                        + "                    \"petersburgBlock\": 4321234,\n"
                        + "                    \"istanbulBlock\": 5435345,\n"
                        + "                    \"clique\": {\n"
                        + "                        \"period\": 15,\n"
                        + "                        \"epoch\": 30000\n"
                        + "                    }\n"
                        + "                },\n"
                        + "                \"head\": \"0x6341fd3daf94b748c72ced5a5b26028f2474f5f00d824504e4fa37a75767e177\"\n"
                        + "            }\n"
                        + "        }\n"
                        + "    }\n"
                        + "}");

        AdminNodeInfo adminNodeInfo = deserialiseResponse(AdminNodeInfo.class);
        assertEquals(
                adminNodeInfo.getResult().getName(),
                ("Geth/v1.9.6-stable-bd059680/linux-amd64/go1.13.1"));
    }

    @Test
    public void testEthProtocolVersion() throws IOException {
        buildResponse(
                "{\n"
                        + "  \"id\":67,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"54\"\n"
                        + "}");

        EthProtocolVersion ethProtocolVersion = deserialiseResponse(EthProtocolVersion.class);
        assertEquals(ethProtocolVersion.getProtocolVersion(), ("54"));
    }

    @Test
    public void testEthSyncingInProgress() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "  \"startingBlock\": \"0x384\",\n"
                        + "  \"currentBlock\": \"0x386\",\n"
                        + "  \"highestBlock\": \"0x454\"\n"
                        + "  }\n"
                        + "}");

        // Response received from Geth node
        // "{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":{\"currentBlock\":\"0x117a\",
        // \"highestBlock\":\"0x21dab4\",\"knownStates\":\"0x0\",\"pulledStates\":\"0x0\",
        // \"startingBlock\":\"0xa51\"}}"

        EthSyncing ethSyncing = deserialiseResponse(EthSyncing.class);

        assertEquals(
                ethSyncing.getResult(),
                (new EthSyncing.Syncing("0x384", "0x386", "0x454", null, null)));
    }

    @Test
    public void testEthSyncing() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": false\n"
                        + "}");

        EthSyncing ethSyncing = deserialiseResponse(EthSyncing.class);
        assertEquals(ethSyncing.isSyncing(), (false));
    }

    @Test
    public void testEthMining() {
        buildResponse(
                "{\n"
                        + "  \"id\":71,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        EthMining ethMining = deserialiseResponse(EthMining.class);
        assertEquals(ethMining.isMining(), (true));
    }

    @Test
    public void testEthHashrate() {
        buildResponse(
                "{\n"
                        + "  \"id\":71,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x38a\"\n"
                        + "}");

        EthHashrate ethHashrate = deserialiseResponse(EthHashrate.class);
        assertEquals(ethHashrate.getHashrate(), (BigInteger.valueOf(906L)));
    }

    @Test
    public void testEthGasPrice() {
        buildResponse(
                "{\n"
                        + "  \"id\":73,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x9184e72a000\"\n"
                        + "}");

        EthGasPrice ethGasPrice = deserialiseResponse(EthGasPrice.class);
        assertEquals(ethGasPrice.getGasPrice(), (BigInteger.valueOf(10000000000000L)));
    }

    @Test
    public void testEthAccounts() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": [\"0x407d73d8a49eeb85d32cf465507dd71d507100c1\"]\n"
                        + "}");

        TolAddresses ethAccounts = deserialiseResponse(TolAddresses.class);
        assertEquals(
                ethAccounts.getAddresses(),
                (Arrays.asList("0x407d73d8a49eeb85d32cf465507dd71d507100c1")));
    }

    @Test
    public void testEthBlockNumber() {
        buildResponse(
                "{\n"
                        + "  \"id\":83,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x4b7\"\n"
                        + "}");

        TolGetBlockCount tolGetBlockCount = deserialiseResponse(TolGetBlockCount.class);
        assertEquals(tolGetBlockCount.getBlockCount(), (BigInteger.valueOf(1207L)));
    }

    @Test
    public void testEthGetBalance() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x234c8a3397aab58\"\n"
                        + "}");

        TolGetBalance tolGetBalance = deserialiseResponse(TolGetBalance.class);
        assertEquals(tolGetBalance.getBalance(), (BigInteger.valueOf(158972490234375000L)));
    }

    @Test
    public void testEthStorageAt() {
        buildResponse(
                "{\n"
                        + "    \"jsonrpc\":\"2.0\","
                        + "    \"id\":1,"
                        + "    \"result\":"
                        + "\"0x000000000000000000000000000000000000000000000000000000000000162e\""
                        + "}");

        EthGetStorageAt ethGetStorageAt = deserialiseResponse(EthGetStorageAt.class);
        assertEquals(
                ethGetStorageAt.getResult(),
                ("0x000000000000000000000000000000000000000000000000000000000000162e"));
    }

    @Test
    public void testEthGetTransactionCount() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x1\"\n"
                        + "}");

        TolGetNonce tolGetNonce = deserialiseResponse((TolGetNonce.class));
        assertEquals(tolGetNonce.getNonce(), (BigInteger.valueOf(1L)));
    }

    @Test
    public void testEthGetBlockTransactionCountByHash() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0xb\"\n"
                        + "}");

        EthGetBlockTransactionCountByHash ethGetBlockTransactionCountByHash =
                deserialiseResponse(EthGetBlockTransactionCountByHash.class);
        assertEquals(
                ethGetBlockTransactionCountByHash.getTransactionCount(), (BigInteger.valueOf(11)));
    }

    @Test
    public void testEthGetBlockTransactionCountByNumber() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0xa\"\n"
                        + "}");

        EthGetBlockTransactionCountByNumber ethGetBlockTransactionCountByNumber =
                deserialiseResponse(EthGetBlockTransactionCountByNumber.class);
        assertEquals(
                ethGetBlockTransactionCountByNumber.getTransactionCount(),
                (BigInteger.valueOf(10)));
    }

    @Test
    public void testEthGetUncleCountByBlockHash() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x1\"\n"
                        + "}");

        EthGetUncleCountByBlockHash ethGetUncleCountByBlockHash =
                deserialiseResponse(EthGetUncleCountByBlockHash.class);
        assertEquals(ethGetUncleCountByBlockHash.getUncleCount(), (BigInteger.valueOf(1)));
    }

    @Test
    public void testEthGetUncleCountByBlockNumber() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x1\"\n"
                        + "}");

        EthGetUncleCountByBlockNumber ethGetUncleCountByBlockNumber =
                deserialiseResponse(EthGetUncleCountByBlockNumber.class);
        assertEquals(ethGetUncleCountByBlockNumber.getUncleCount(), (BigInteger.valueOf(1)));
    }

    @Test
    public void testGetCode() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x600160008035811a818181146012578301005b601b60013560255"
                        + "65b8060005260206000f25b600060078202905091905056\"\n"
                        + "}");

        EthGetCode ethGetCode = deserialiseResponse(EthGetCode.class);
        assertEquals(
                ethGetCode.getCode(),
                ("0x600160008035811a818181146012578301005b601b60013560255"
                        + "65b8060005260206000f25b600060078202905091905056"));
    }

    @Test
    public void testEthSign() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": "
                        + "\"0xbd685c98ec39490f50d15c67ba2a8e9b5b1d6d7601fca80b295e7d717446bd8b712"
                        + "7ea4871e996cdc8cae7690408b4e800f60ddac49d2ad34180e68f1da0aaf001\"\n"
                        + "}");

        EthSign ethSign = deserialiseResponse(EthSign.class);
        assertEquals(
                ethSign.getSignature(),
                ("0xbd685c98ec39490f50d15c67ba2a8e9b5b1d6d7601fca80b295e7d717446bd8b7127ea4871e9"
                        + "96cdc8cae7690408b4e800f60ddac49d2ad34180e68f1da0aaf001"));
    }

    @Test
    public void testEthSendTransaction() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": "
                        + "\"0xe670ec64341771606e55d6b4ca35a1a6b75ee3d5145a99d05921026d1527331\"\n"
                        + "}");

        AccountSendRawTransaction accountSendRawTransaction =
                deserialiseResponse(AccountSendRawTransaction.class);
        assertEquals(
                accountSendRawTransaction.getTransactionHash(),
                ("0xe670ec64341771606e55d6b4ca35a1a6b75ee3d5145a99d05921026d1527331"));
    }

    @Test
    public void testEthSendRawTransaction() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": "
                        + "\"0xe670ec64341771606e55d6b4ca35a1a6b75ee3d5145a99d05921026d1527331\"\n"
                        + "}");

        EthSendRawTransaction ethSendRawTransaction =
                deserialiseResponse(EthSendRawTransaction.class);
        assertEquals(
                ethSendRawTransaction.getTransactionHash(),
                ("0xe670ec64341771606e55d6b4ca35a1a6b75ee3d5145a99d05921026d1527331"));
    }

    @Test
    public void testEthCall() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x\"\n"
                        + "}");

        TolTryCallTransaction tolTryCallTransaction =
                deserialiseResponse(TolTryCallTransaction.class);
        assertEquals(tolTryCallTransaction.getOutput(), ("0x"));
        assertFalse(tolTryCallTransaction.isReverted());
        assertNull(tolTryCallTransaction.getRevertReason());
    }

    @Test
    public void testEthCallReverted() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x08c379a0"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "00000000000000000000000000000000000000000000000000000000000000ee"
                        + "536f6c696469747920757365732073746174652d726576657274696e67206578"
                        + "63657074696f6e7320746f2068616e646c65206572726f72732e205468652072"
                        + "6571756972652066756e6374696f6e2073686f756c6420626520757365642074"
                        + "6f20656e737572652076616c696420636f6e646974696f6e732c207375636820"
                        + "617320696e707574732c206f7220636f6e747261637420737461746520766172"
                        + "6961626c657320617265206d65742c206f7220746f2076616c69646174652072"
                        + "657475726e2076616c7565732066726f6d2063616c6c7320746f206578746572"
                        + "6e616c20636f6e7472616374732e000000000000000000000000000000000000\"\n"
                        + "}");

        TolTryCallTransaction tolTryCallTransaction =
                deserialiseResponse(TolTryCallTransaction.class);
        assertTrue(tolTryCallTransaction.isReverted());
        assertEquals(
                tolTryCallTransaction.getRevertReason(),
                ("Solidity uses state-reverting exceptions to "
                        + "handle errors. The require function should be "
                        + "used to ensure valid conditions, such as inputs, "
                        + "or contract state variables are met, or to "
                        + "validate return values from calls to "
                        + "external contracts."));
    }

    @Test
    public void testEthEstimateGas() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x5208\"\n"
                        + "}");

        TolGetGasEstimate tolGetGasEstimate = deserialiseResponse(TolGetGasEstimate.class);
        assertEquals(tolGetGasEstimate.getGasEstimate(), (BigInteger.valueOf(21000)));
    }

    @Test
    public void testEthBlockTransactionHashes() {}

    @Test
    public void testEthBlockFullTransactionsParity() {}

    // Remove once Geth & Parity return the same v value in transactions
    @Test
    public void testEthBlockFullTransactionsGeth() {}

    @Test
    public void testEthBlockNull() {
        buildResponse("{\n" + "  \"result\": null\n" + "}");

        TolBlock tolBlock = deserialiseResponse(TolBlock.class);
        assertNull(tolBlock.getBlock());
    }

    @Test
    public void testEthTransaction() {}

    @Test
    public void testTransactionChainId() {}

    @Test
    public void testTransactionLongChainId() {}

    @Test
    public void testEthTransactionNull() {
        buildResponse("{\n" + "  \"result\": null\n" + "}");

        TolTransaction tolTransaction = deserialiseResponse(TolTransaction.class);
        assertEquals(tolTransaction.getTransaction(), (Optional.empty()));
    }

    @Test
    public void testeEthGetTransactionReceiptBeforeByzantium() {}

    @Test
    public void testeEthGetTransactionReceiptAfterByzantium() {}

    @Test
    public void testTransactionReceiptIsStatusOK() {}

    @Test
    public void testEthGetCompilers() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": [\"solidity\", \"lll\", \"serpent\"]\n"
                        + "}");

        EthGetCompilers ethGetCompilers = deserialiseResponse(EthGetCompilers.class);
        assertEquals(ethGetCompilers.getCompilers(), (Arrays.asList("solidity", "lll", "serpent")));
    }

    @Test
    public void testEthCompileSolidity() {

        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"test\": {\n"
                        + "      \"code\": \"0x605280600c6000396000f3006000357c010000000000000000000000000000000000000000000000000000000090048063c6888fa114602e57005b60376004356041565b8060005260206000f35b6000600782029050604d565b91905056\",\n"
                        + "      \"info\": {\n"
                        + "        \"source\": \"contract test {\\n\\tfunction multiply(uint a) returns(uint d) {\\n\\t\\treturn a * 7;\\n\\t}\\n}\\n\",\n"
                        + "        \"language\": \"Solidity\",\n"
                        + "        \"languageVersion\": \"0\",\n"
                        + "        \"compilerVersion\": \"0.8.2\",\n"
                        + "        \"compilerOptions\": \"--bin --abi --userdoc --devdoc --add-std --optimize -o /var/folders/3m/_6gnl12n1tj_5kf7sc3d72dw0000gn/T/solc498936951\",\n"
                        + "        \"abiDefinition\": [\n"
                        + "          {\n"
                        + "            \"constant\": false,\n"
                        + "            \"inputs\": [\n"
                        + "              {\n"
                        + "                \"name\": \"a\",\n"
                        + "                \"type\": \"uint256\"\n"
                        + "              }\n"
                        + "            ],\n"
                        + "            \"name\": \"multiply\",\n"
                        + "            \"outputs\": [\n"
                        + "              {\n"
                        + "                \"name\": \"d\",\n"
                        + "                \"type\": \"uint256\"\n"
                        + "              }\n"
                        + "            ],\n"
                        + "            \"type\": \"function\",\n"
                        + "            \"payable\": false\n"
                        + "          }\n"
                        + "        ],\n"
                        + "        \"userDoc\": {\n"
                        + "          \"methods\": {}\n"
                        + "        },\n"
                        + "        \"developerDoc\": {\n"
                        + "          \"methods\": {}\n"
                        + "        }\n"
                        + "      }\n"
                        + "    }\n"
                        + "    }"
                        + "  }\n"
                        + "}");

        Map<String, EthCompileSolidity.Code> compiledSolidity = new HashMap<>(1);
        compiledSolidity.put(
                "test",
                new EthCompileSolidity.Code(
                        "0x605280600c6000396000f3006000357c010000000000000000000000000000000000000000000000000000000090048063c6888fa114602e57005b60376004356041565b8060005260206000f35b6000600782029050604d565b91905056",
                        new EthCompileSolidity.SolidityInfo(
                                "contract test {\n\tfunction multiply(uint a) returns(uint d) {\n"
                                        + "\t\treturn a * 7;\n\t}\n}\n",
                                "Solidity",
                                "0",
                                "0.8.2",
                                "--bin --abi --userdoc --devdoc --add-std --optimize -o "
                                        + "/var/folders/3m/_6gnl12n1tj_5kf7sc3d72dw0000gn/T/solc498936951",
                                Arrays.asList(
                                        new AbiDefinition(
                                                false,
                                                Arrays.asList(
                                                        new AbiDefinition.NamedType(
                                                                "a", "uint256")),
                                                "multiply",
                                                Arrays.asList(
                                                        new AbiDefinition.NamedType(
                                                                "d", "uint256")),
                                                "function",
                                                false)),
                                new EthCompileSolidity.Documentation(),
                                new EthCompileSolidity.Documentation())));

        EthCompileSolidity ethCompileSolidity = deserialiseResponse(EthCompileSolidity.class);
        assertEquals(ethCompileSolidity.getCompiledSolidity(), (compiledSolidity));
    }

    @Test
    public void testEthCompileLLL() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x603880600c6000396000f3006001600060e060020a60003504806"
                        + "3c6888fa114601857005b6021600435602b565b8060005260206000f35b600081600702"
                        + "905091905056\"\n"
                        + "}");

        EthCompileLLL ethCompileLLL = deserialiseResponse(EthCompileLLL.class);
        assertEquals(
                ethCompileLLL.getCompiledSourceCode(),
                ("0x603880600c6000396000f3006001600060e060020a600035048063c6888fa114601857005b60"
                        + "21600435602b565b8060005260206000f35b600081600702905091905056"));
    }

    @Test
    public void testEthCompileSerpent() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x603880600c6000396000f3006001600060e060020a60003504806"
                        + "3c6888fa114601857005b6021600435602b565b8060005260206000f35b600081600702"
                        + "905091905056\"\n"
                        + "}");

        EthCompileSerpent ethCompileSerpent = deserialiseResponse(EthCompileSerpent.class);
        assertEquals(
                ethCompileSerpent.getCompiledSourceCode(),
                ("0x603880600c6000396000f3006001600060e060020a600035048063c6888fa114601857005b60"
                        + "21600435602b565b8060005260206000f35b600081600702905091905056"));
    }

    @Test
    public void testEthFilter() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x1\"\n"
                        + "}");

        EthFilter ethFilter = deserialiseResponse(EthFilter.class);
        assertEquals(ethFilter.getFilterId(), (BigInteger.valueOf(1)));
    }

    @Test
    public void testEthUninstallFilter() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        EthUninstallFilter ethUninstallFilter = deserialiseResponse(EthUninstallFilter.class);
        assertEquals(ethUninstallFilter.isUninstalled(), (true));
    }

    @Test
    public void testEthLog() {

        buildResponse(
                "{\n"
                        + "    \"id\":1,\n"
                        + "    \"jsonrpc\":\"2.0\",\n"
                        + "    \"result\": [{\n"
                        + "        \"removed\": false,\n"
                        + "        \"logIndex\": \"0x1\",\n"
                        + "        \"transactionIndex\": \"0x0\",\n"
                        + "        \"transactionHash\": \"0xdf829c5a142f1fccd7d8216c5785ac562ff41e2dcfdf5785ac562ff41e2dcf\",\n"
                        + "        \"blockHash\": \"0x8216c5785ac562ff41e2dcfdf5785ac562ff41e2dcfdf829c5a142f1fccd7d\",\n"
                        + "        \"blockNumber\":\"0x1b4\",\n"
                        + "        \"address\": \"0x16c5785ac562ff41e2dcfdf829c5a142f1fccd7d\",\n"
                        + "        \"data\":\"0x0000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"type\":\"mined\",\n"
                        + "        \"topics\": [\"0x59ebeb90bc63057b6515673c3ecf9438e5058bca0f92585014eced636878c9a5\"]"
                        + "    }]"
                        + "}");

        List<Log> logs =
                Collections.singletonList(
                        new EthLog.LogObject(
                                false,
                                "0x1",
                                "0x0",
                                "0xdf829c5a142f1fccd7d8216c5785ac562ff41e2dcfdf5785ac562ff41e2dcf",
                                "0x8216c5785ac562ff41e2dcfdf5785ac562ff41e2dcfdf829c5a142f1fccd7d",
                                "0x1b4",
                                "0x16c5785ac562ff41e2dcfdf829c5a142f1fccd7d",
                                "0x0000000000000000000000000000000000000000000000000000000000000000",
                                "mined",
                                Collections.singletonList(
                                        "0x59ebeb90bc63057b6515673c3ecf9438e5058bca0f92585014eced636878c9a5")));

        EthLog ethLog = deserialiseResponse(EthLog.class);
        assertEquals(ethLog.getLogs(), (logs));
    }

    @Test
    public void testEthGetWork() {

        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": [\n"
                        + "      \"0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef\",\n"
                        + "      \"0x5EED00000000000000000000000000005EED0000000000000000000000000000\",\n"
                        + "      \"0xd1ff1c01710000000000000000000000d1ff1c01710000000000000000000000\"\n"
                        + "    ]\n"
                        + "}");

        EthGetWork ethGetWork = deserialiseResponse(EthGetWork.class);
        assertEquals(
                ethGetWork.getCurrentBlockHeaderPowHash(),
                ("0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef"));
        assertEquals(
                ethGetWork.getSeedHashForDag(),
                ("0x5EED00000000000000000000000000005EED0000000000000000000000000000"));
        assertEquals(
                ethGetWork.getBoundaryCondition(),
                ("0xd1ff1c01710000000000000000000000d1ff1c01710000000000000000000000"));
    }

    @Test
    public void testEthSubmitWork() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        EthSubmitWork ethSubmitWork = deserialiseResponse(EthSubmitWork.class);
        assertEquals(ethSubmitWork.solutionValid(), (true));
    }

    @Test
    public void testEthSubmitHashrate() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        EthSubmitHashrate ethSubmitHashrate = deserialiseResponse(EthSubmitHashrate.class);
        assertEquals(ethSubmitHashrate.submissionSuccessful(), (true));
    }

    @Test
    public void testDbPutString() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        DbPutString dbPutString = deserialiseResponse(DbPutString.class);
        assertEquals(dbPutString.valueStored(), (true));
    }

    @Test
    public void testDbGetString() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": \"myString\"\n"
                        + "}");

        DbGetString dbGetString = deserialiseResponse(DbGetString.class);
        assertEquals(dbGetString.getStoredValue(), ("myString"));
    }

    @Test
    public void testDbPutHex() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        DbPutHex dbPutHex = deserialiseResponse(DbPutHex.class);
        assertEquals(dbPutHex.valueStored(), (true));
    }

    @Test
    public void testDbGetHex() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": \"0x68656c6c6f20776f726c64\"\n"
                        + "}");

        DbGetHex dbGetHex = deserialiseResponse(DbGetHex.class);
        assertEquals(dbGetHex.getStoredValue(), ("0x68656c6c6f20776f726c64"));
    }

    @Test
    public void testSshVersion() {
        buildResponse(
                "{\n"
                        + "  \"id\":67,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"2\"\n"
                        + "}");

        ShhVersion shhVersion = deserialiseResponse(ShhVersion.class);
        assertEquals(shhVersion.getVersion(), ("2"));
    }

    @Test
    public void testSshPost() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        ShhPost shhPost = deserialiseResponse(ShhPost.class);
        assertEquals(shhPost.messageSent(), (true));
    }

    @Test
    public void testSshNewIdentity() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": "
                        + "\"0xc931d93e97ab07fe42d923478ba2465f283f440fd6cabea4dd7a2c807108f651b713"
                        + "5d1d6ca9007d5b68aa497e4619ac10aa3b27726e1863c1fd9b570d99bbaf\"\n"
                        + "}");

        ShhNewIdentity shhNewIdentity = deserialiseResponse(ShhNewIdentity.class);
        assertEquals(
                shhNewIdentity.getAddress(),
                ("0xc931d93e97ab07fe42d923478ba2465f283f440fd6cabea4dd7a2c807108f651b7135d1d6ca9"
                        + "007d5b68aa497e4619ac10aa3b27726e1863c1fd9b570d99bbaf"));
    }

    @Test
    public void testSshHasIdentity() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        ShhHasIdentity shhHasIdentity = deserialiseResponse(ShhHasIdentity.class);
        assertEquals(shhHasIdentity.hasPrivateKeyForIdentity(), (true));
    }

    @Test
    public void testSshNewGroup() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": "
                        + "\"0xc65f283f440fd6cabea4dd7a2c807108f651b7135d1d6ca90931d93e97ab07fe42d9"
                        + "23478ba2407d5b68aa497e4619ac10aa3b27726e1863c1fd9b570d99bbaf\"\n"
                        + "}");

        ShhNewGroup shhNewGroup = deserialiseResponse(ShhNewGroup.class);
        assertEquals(
                shhNewGroup.getAddress(),
                ("0xc65f283f440fd6cabea4dd7a2c807108f651b7135d1d6ca90931d93e97ab07fe42d923478ba24"
                        + "07d5b68aa497e4619ac10aa3b27726e1863c1fd9b570d99bbaf"));
    }

    @Test
    public void testSshAddToGroup() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        ShhAddToGroup shhAddToGroup = deserialiseResponse(ShhAddToGroup.class);
        assertEquals(shhAddToGroup.addedToGroup(), (true));
    }

    @Test
    public void testSshNewFilter() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": \"0x7\"\n"
                        + "}");

        ShhNewFilter shhNewFilter = deserialiseResponse(ShhNewFilter.class);
        assertEquals(shhNewFilter.getFilterId(), (BigInteger.valueOf(7)));
    }

    @Test
    public void testSshUninstallFilter() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": true\n"
                        + "}");

        ShhUninstallFilter shhUninstallFilter = deserialiseResponse(ShhUninstallFilter.class);
        assertEquals(shhUninstallFilter.isUninstalled(), (true));
    }

    @Test
    public void testSshMessages() {
        buildResponse(
                "{\n"
                        + "  \"id\":1,\n"
                        + "  \"jsonrpc\":\"2.0\",\n"
                        + "  \"result\": [{\n"
                        + "    \"hash\": \"0x33eb2da77bf3527e28f8bf493650b1879b08c4f2a362beae4ba2f"
                        + "71bafcd91f9\",\n"
                        + "    \"from\": \"0x3ec052fc33...\",\n"
                        + "    \"to\": \"0x87gdf76g8d7fgdfg...\",\n"
                        + "    \"expiry\": \"0x54caa50a\",\n"
                        + "    \"ttl\": \"0x64\",\n"
                        + "    \"sent\": \"0x54ca9ea2\",\n"
                        + "    \"topics\": [\"0x6578616d\"],\n"
                        + "    \"payload\": \"0x7b2274797065223a226d657373616765222c2263686...\",\n"
                        + "    \"workProved\": \"0x0\"\n"
                        + "    }]\n"
                        + "}");

        List<ShhMessages.SshMessage> messages =
                Arrays.asList(
                        new ShhMessages.SshMessage(
                                "0x33eb2da77bf3527e28f8bf493650b1879b08c4f2a362beae4ba2f71bafcd91f9",
                                "0x3ec052fc33...",
                                "0x87gdf76g8d7fgdfg...",
                                "0x54caa50a",
                                "0x64",
                                "0x54ca9ea2",
                                Arrays.asList("0x6578616d"),
                                "0x7b2274797065223a226d657373616765222c2263686...",
                                "0x0"));

        ShhMessages shhMessages = deserialiseResponse(ShhMessages.class);
        assertEquals(shhMessages.getMessages(), (messages));
    }
}
