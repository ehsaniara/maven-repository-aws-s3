/*
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
import com.amazonaws.regions.AwsRegionProvider;

public class MavenSettingsRegionProvider extends AwsRegionProvider {

    public final String settingsRegion;

    public MavenSettingsRegionProvider(String settingsRegion) {
        this.settingsRegion = settingsRegion;
    }

    @Override
    public String getRegion() throws SdkClientException {
        return settingsRegion;
    }
}
