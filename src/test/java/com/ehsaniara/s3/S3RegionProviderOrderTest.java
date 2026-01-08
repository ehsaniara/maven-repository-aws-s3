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

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import static org.junit.jupiter.api.Assertions.*;

class S3RegionProviderOrderTest {

    @Test
    void getRegion_withProvidedRegion_returnsProvidedRegion() {
        S3RegionProviderOrder provider = new S3RegionProviderOrder("us-east-1");

        Region region = provider.getRegion();

        assertNotNull(region);
        assertEquals("us-east-1", region.id());
    }

    @Test
    void getRegion_withProvidedRegion_prioritizesProvidedRegion() {
        // Even if environment has a different region, provided region should take precedence
        S3RegionProviderOrder provider = new S3RegionProviderOrder("eu-west-1");

        Region region = provider.getRegion();

        assertEquals(Region.EU_WEST_1, region);
    }

    @Test
    void getRegionString_withProvidedRegion_returnsString() {
        S3RegionProviderOrder provider = new S3RegionProviderOrder("ap-northeast-1");

        String regionString = provider.getRegionString();

        assertEquals("ap-northeast-1", regionString);
    }

    @Test
    void getRegionString_withNullProvidedRegion_fallsBackToChain() {
        S3RegionProviderOrder provider = new S3RegionProviderOrder(null);

        // Should either return null or a region from the fallback chain
        // depending on environment configuration
        String regionString = provider.getRegionString();

        // Just verify it doesn't throw an exception
        // The actual value depends on the environment
        assertTrue(regionString == null || !regionString.isEmpty());
    }

    @Test
    void constructor_createsProviderChain() {
        // Should not throw any exception
        S3RegionProviderOrder provider = new S3RegionProviderOrder("us-west-2");

        assertNotNull(provider);
    }
}
