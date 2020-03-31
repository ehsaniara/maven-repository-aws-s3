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

import java.util.Objects;

public class PublicReadProperty {

    // first priority: from .m2/setting.xml configuration parameters: <publicRepository>false</publicRepository>
    private static final String PUBLIC_REPOSITORY_PROP_TAG = "publicRepository";

    //second priority: get it from environment
    private static final String PUBLIC_REPOSITORY_ENV_TAG = "PUBLIC_REPOSITORY";

    private Boolean publicRepository;

    public PublicReadProperty(Boolean publicRepository) {
        this.publicRepository = publicRepository;
    }

    public boolean get() {
        if (Objects.nonNull(publicRepository))
            return publicRepository;

        String publicRepositoryPropTag = System.getProperty(PUBLIC_REPOSITORY_PROP_TAG);
        if (Objects.nonNull(publicRepositoryPropTag)) {
            return Boolean.parseBoolean(publicRepositoryPropTag);
        }

        String publicRepositoryEnvTag = System.getenv(PUBLIC_REPOSITORY_ENV_TAG);
        if (Objects.nonNull(publicRepositoryEnvTag)) {
            return Boolean.parseBoolean(publicRepositoryEnvTag);
        }

        return false;
    }

}
