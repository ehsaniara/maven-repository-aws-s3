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

import lombok.extern.java.Log;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

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
     * @param profile         an optional AWS named profile from ~/.aws/credentials or ~/.aws/config.
     * @return a {@link software.amazon.awssdk.auth.credentials.AwsCredentialsProvider} object.
     */
    public AwsCredentialsProvider connect(AuthenticationInfo authenticationInfo, String profile) {
        if (!Objects.isNull(authenticationInfo)) {
            log.info("AWS Connection By StaticCredentialsProvider class");
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                            authenticationInfo.getUserName(),
                            authenticationInfo.getPassword()));
        } else if (profile != null && !profile.isEmpty()) {
            log.info(String.format("AWS Connection By ProfileCredentialsProvider using profile '%s'", profile));
            return ProfileCredentialsProvider.create(profile);
        } else {
            return DefaultCredentialsProvider.builder().build();
        }
    }
}
