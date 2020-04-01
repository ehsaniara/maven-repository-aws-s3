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
import lombok.extern.java.Log;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import java.util.Objects;

/**
 * S3Connect s3Connect
 */
@Log
public class S3Connect {

    /**
     * @param authenticationInfo authenticationInfo
     * @param region             region
     * @param endpoint           endpoint
     * @param pathStyle          pathStyle
     * @return AmazonS3
     * @throws AuthenticationException AuthenticationException
     */
    public static AmazonS3 connect(AuthenticationInfo authenticationInfo, String region, EndpointProperty endpoint, PathStyleEnabledProperty pathStyle) throws AuthenticationException {

        AmazonS3ClientBuilder builder = null;
        try {
            builder = createAmazonS3ClientBuilder(authenticationInfo, region, endpoint, pathStyle);

            AmazonS3 amazonS3 = builder.build();

            log.finer(String.format("Connected to S3 using bucket %s.", endpoint.get()));

            return amazonS3;
        } catch (SdkClientException e) {
            if (Objects.nonNull(builder)) {
                StringBuilder errorMessage = new StringBuilder();
                errorMessage.append("Failed to connect");
                if (Objects.nonNull(builder.getEndpoint())) {
                    errorMessage.append(
                            String.format(" to endpoint [%s] using region [%s]",
                                    builder.getEndpoint().getServiceEndpoint(),
                                    builder.getEndpoint().getSigningRegion()));

                } else {
                    errorMessage.append(String.format(" using region [%s]", builder.getRegion()));
                }
                throw new AuthenticationException(errorMessage.toString(), e);
            }
            throw new AuthenticationException("Could not authenticate", e);
        }
    }

    /**
     * @param authenticationInfo authenticationInfo
     * @param region             region
     * @param endpoint           endpoint
     * @param pathStyle          pathStyle
     * @return AmazonS3ClientBuilder
     */
    private static AmazonS3ClientBuilder createAmazonS3ClientBuilder(AuthenticationInfo authenticationInfo, String region, EndpointProperty endpoint, PathStyleEnabledProperty pathStyle) {
        final S3RegionProviderOrder regionProvider = new S3RegionProviderOrder(region);

        AmazonS3ClientBuilder builder;
        builder = AmazonS3ClientBuilder.standard().withCredentials(new AwsCredentialsFactory().connect(authenticationInfo));

        if (endpoint.isPresent()) {
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint.get(), builder.getRegion()));
        } else {
            builder.setRegion(regionProvider.getRegion());
        }

        builder.setPathStyleAccessEnabled(pathStyle.get());
        return builder;
    }
}
