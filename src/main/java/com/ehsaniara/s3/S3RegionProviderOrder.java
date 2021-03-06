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

import com.amazonaws.regions.*;

/**
 * this class extends AwsRegionProviderChain
 *
 * @author jay
 * @version $Id: $Id
 */
public class S3RegionProviderOrder extends AwsRegionProviderChain {

    /**
     * <p>Constructor for S3RegionProviderOrder.</p>
     *
     * @param providedRegion a {@link java.lang.String} object.
     */
    public S3RegionProviderOrder(final String providedRegion) {
        super(new MavenSettingsRegionProvider(providedRegion),
                new AwsDefaultEnvRegionProvider(),
                new AwsEnvVarOverrideRegionProvider(),
                new AwsSystemPropertyRegionProvider(),
                new AwsProfileRegionProvider(),
                new InstanceMetadataRegionProvider());
    }

}
