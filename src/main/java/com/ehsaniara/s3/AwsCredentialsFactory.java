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

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import lombok.extern.java.Log;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import java.util.Objects;

/**
 * <p>AwsCredentialsFactory class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
@Log
public class AwsCredentialsFactory {

    /**
     * <p>connect.</p>
     *
     * @param authenticationInfo a {@link org.apache.maven.wagon.authentication.AuthenticationInfo} object.
     * @return a {@link com.amazonaws.auth.AWSCredentialsProvider} object.
     */
    public AWSCredentialsProvider connect(AuthenticationInfo authenticationInfo) {
        if (Objects.isNull(authenticationInfo)) {
            return new DefaultAWSCredentialsProviderChain();
        } else {
            log.info("AWS Connection By AWSStaticCredentialsProvider class");
            return new AWSStaticCredentialsProvider(new BasicAWSCredentials(authenticationInfo.getUserName(), authenticationInfo.getPassword()));
        }
    }
}
