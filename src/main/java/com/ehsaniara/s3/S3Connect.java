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
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * S3Connect s3Connect
 *
 * @author jay
 * @version $Id: $Id
 */
@Log
public class S3Connect {

    /**
     * <p>connect.</p>
     *
     * @param authenticationInfo authenticationInfo
     * @param region             region
     * @param endpoint           endpoint
     * @param pathStyle          pathStyle
     * @return S3Client
     * @throws org.apache.maven.wagon.authentication.AuthenticationException org.apache.maven.wagon.authentication.AuthenticationException
     */
    public static S3Client connect(AuthenticationInfo authenticationInfo, String region, EndpointProperty endpoint, PathStyleEnabledProperty pathStyle) throws AuthenticationException {

        try {
            S3Client s3Client = createS3Client(authenticationInfo, region, endpoint, pathStyle);

            log.finer(String.format("Connected to S3 using endpoint %s.", endpoint.isPresent() ? endpoint.get() : "default"));

            return s3Client;
        } catch (SdkClientException e) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Failed to connect");
            if (endpoint.isPresent()) {
                errorMessage.append(String.format(" to endpoint [%s]", endpoint.get()));
            }
            if (region != null) {
                errorMessage.append(String.format(" using region [%s]", region));
            }
            throw new AuthenticationException(errorMessage.toString(), e);
        }
    }

    /**
     * @param authenticationInfo authenticationInfo
     * @param region             region
     * @param endpoint           endpoint
     * @param pathStyle          pathStyle
     * @return S3Client
     */
    private static S3Client createS3Client(AuthenticationInfo authenticationInfo, String region, EndpointProperty endpoint, PathStyleEnabledProperty pathStyle) {
        final S3RegionProviderOrder regionProvider = new S3RegionProviderOrder(region);

        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(pathStyle.get())
                .build();

        S3ClientBuilder builder = S3Client.builder()
                .credentialsProvider(new AwsCredentialsFactory().connect(authenticationInfo))
                .serviceConfiguration(s3Config);

        String regionId = regionProvider.getRegionString();
        if (regionId != null) {
            builder.region(Region.of(regionId));
        } else {
            // Default fallback region
            builder.region(Region.US_EAST_1);
        }

        if (endpoint.isPresent()) {
            builder.endpointOverride(URI.create(endpoint.get()));
        }

        return builder.build();
    }
}
