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
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import java.util.logging.Logger;

public class AwsCredentialsFactory {

    private static final Logger LOGGER = Logger.getLogger(AwsCredentialsFactory.class.getName());

    public AWSCredentialsProvider create(AuthenticationInfo authenticationInfo) {
        if (authenticationInfo == null) {
            return new DefaultAWSCredentialsProviderChain();
        } else {
            LOGGER.info("By AWSStaticCredentialsProvider");
            return new AWSStaticCredentialsProvider(new BasicAWSCredentials(authenticationInfo.getUserName(), authenticationInfo.getPassword()));
        }
    }
}