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

import com.amazonaws.SdkClientException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import java.util.logging.Logger;

/**
 * S3Connect s3Connect
 */
public class S3Connect {

    private static final Logger LOGGER = Logger.getLogger(S3Connect.class.getName());

    public static AmazonS3 connect(AuthenticationInfo authenticationInfo, String region, EndpointProperty endpoint, PathStyleEnabledProperty pathStyle) throws AuthenticationException {

        AmazonS3ClientBuilder builder = null;
        try {
            builder = createAmazonS3ClientBuilder(authenticationInfo, region, endpoint, pathStyle);

            AmazonS3 amazonS3 = builder.build();

            LOGGER.finer(String.format("Connected to S3 using bucket %s.", endpoint.get()));

            return amazonS3;
        } catch (SdkClientException e) {
            if (builder != null) {
                StringBuilder message = new StringBuilder();
                message.append("Failed to connect");
                if (builder.getEndpoint() != null) {
                    message.append(
                            String.format(" to endpoint [%s] using region [%s]",
                                    builder.getEndpoint().getServiceEndpoint(),
                                    builder.getEndpoint().getSigningRegion()));

                } else {
                    message.append(String.format(" using region [%s]", builder.getRegion()));
                }
                throw new AuthenticationException(message.toString(), e);
            }
            throw new AuthenticationException("Could not authenticate", e);
        }
    }

    private static AmazonS3ClientBuilder createAmazonS3ClientBuilder(AuthenticationInfo authenticationInfo, String region, EndpointProperty endpoint, PathStyleEnabledProperty pathStyle) {
        final S3StorageRegionProviderChain regionProvider = new S3StorageRegionProviderChain(region);

        AmazonS3ClientBuilder builder;
        builder = AmazonS3ClientBuilder.standard().withCredentials(new AwsCredentialsFactory().create(authenticationInfo));

        if (endpoint.isPresent()) {
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint.get(), builder.getRegion()));
        } else {
            builder.setRegion(regionProvider.getRegion());
        }

        builder.setPathStyleAccessEnabled(pathStyle.get());
        return builder;
    }
}
