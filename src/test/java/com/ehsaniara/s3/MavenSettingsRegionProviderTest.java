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

class MavenSettingsRegionProviderTest {

    @Test
    void getRegion_withValidRegion_returnsRegion() {
        MavenSettingsRegionProvider provider = new MavenSettingsRegionProvider("us-west-2");

        Region region = provider.getRegion();

        assertNotNull(region);
        assertEquals("us-west-2", region.id());
    }

    @Test
    void getRegion_withNullRegion_returnsNull() {
        MavenSettingsRegionProvider provider = new MavenSettingsRegionProvider(null);

        Region region = provider.getRegion();

        assertNull(region);
    }

    @Test
    void getRegion_withEuRegion_returnsCorrectRegion() {
        MavenSettingsRegionProvider provider = new MavenSettingsRegionProvider("eu-central-1");

        Region region = provider.getRegion();

        assertNotNull(region);
        assertEquals(Region.EU_CENTRAL_1, region);
    }

    @Test
    void settingsRegion_fieldIsAccessible() {
        MavenSettingsRegionProvider provider = new MavenSettingsRegionProvider("ap-southeast-1");

        assertEquals("ap-southeast-1", provider.settingsRegion);
    }
}
