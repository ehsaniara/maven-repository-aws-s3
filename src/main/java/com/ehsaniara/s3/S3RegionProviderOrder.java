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

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Custom region provider chain that prioritizes Maven settings,
 * then falls back to AWS SDK's default resolution order.
 *
 * @author jay
 * @version $Id: $Id
 */
public class S3RegionProviderOrder implements AwsRegionProvider {

    private final List<AwsRegionProvider> providers;

    /**
     * <p>Constructor for S3RegionProviderOrder.</p>
     *
     * @param providedRegion a {@link java.lang.String} object.
     */
    public S3RegionProviderOrder(final String providedRegion) {
        this.providers = Arrays.asList(
                new MavenSettingsRegionProvider(providedRegion),
                new AwsDefaultEnvRegionProvider(),
                DefaultAwsRegionProviderChain.builder().build()
        );
    }

    /** {@inheritDoc} */
    @Override
    public Region getRegion() {
        for (AwsRegionProvider provider : providers) {
            try {
                Region region = provider.getRegion();
                if (region != null) {
                    return region;
                }
            } catch (Exception e) {
                // Continue to next provider
            }
        }
        return null;
    }

    /**
     * Get region as String for backward compatibility.
     *
     * @return the region string or null
     */
    public String getRegionString() {
        Region region = getRegion();
        return region != null ? region.id() : null;
    }
}
