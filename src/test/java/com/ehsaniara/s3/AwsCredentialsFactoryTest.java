/*
 * Copyright 2020 Jay Ehsaniara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ehsaniara.s3;

import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import static org.junit.jupiter.api.Assertions.*;

class AwsCredentialsFactoryTest {

    private AwsCredentialsFactory factory;

    @BeforeEach
    void setUp() {
        factory = new AwsCredentialsFactory();
    }

    @Test
    void connect_withNullAuthenticationInfo_returnsDefaultCredentialsProvider() {
        AwsCredentialsProvider provider = factory.connect(null);

        assertNotNull(provider);
        assertTrue(provider instanceof DefaultCredentialsProvider,
                "Expected DefaultCredentialsProvider when no authentication info provided");
    }

    @Test
    void connect_withAuthenticationInfo_returnsStaticCredentialsProvider() {
        AuthenticationInfo authInfo = new AuthenticationInfo();
        authInfo.setUserName("testAccessKey");
        authInfo.setPassword("testSecretKey");

        AwsCredentialsProvider provider = factory.connect(authInfo);

        assertNotNull(provider);
        assertTrue(provider instanceof StaticCredentialsProvider,
                "Expected StaticCredentialsProvider when authentication info provided");
    }

    @Test
    void connect_withAuthenticationInfo_credentialsContainCorrectValues() {
        AuthenticationInfo authInfo = new AuthenticationInfo();
        authInfo.setUserName("myAccessKey");
        authInfo.setPassword("mySecretKey");

        AwsCredentialsProvider provider = factory.connect(authInfo);

        assertEquals("myAccessKey", provider.resolveCredentials().accessKeyId());
        assertEquals("mySecretKey", provider.resolveCredentials().secretAccessKey());
    }
}
