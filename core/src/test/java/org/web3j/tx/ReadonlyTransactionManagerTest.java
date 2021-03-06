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

import org.junit.jupiter.api.Test;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.TolTryCallTransaction;

import static org.mockito.Mockito.mock;

public class ReadonlyTransactionManagerTest {

    private static final String OWNER_REVERT_MSG_STR =
            "Only the contract owner can perform this action";

    Web3jService service = mock(Web3jService.class);
    Web3j web3j = Web3j.build(service);
    DefaultBlockParameter defaultBlockParameter = mock(DefaultBlockParameter.class);
    TolTryCallTransaction response = mock(TolTryCallTransaction.class);

    @Test
    public void sendCallTest() throws IOException {}

    @Test
    public void sendCallRevertedTest() throws IOException {}

    @Test
    public void testSendTransaction() {}
}
