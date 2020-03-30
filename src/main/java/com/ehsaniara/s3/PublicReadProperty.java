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

public class PublicReadProperty {

    private static final String PUBLIC_REPOSITORY_PROP_TAG = "publicRepository";
    private static final String PUBLIC_REPOSITORY_ENV_TAG = "PUBLIC_REPOSITORY";

    private Boolean publicRepository;

    public PublicReadProperty(Boolean publicRepository) {
        this.publicRepository = publicRepository;
    }

    public boolean get() {
        if (publicRepository != null) {
            return publicRepository;
        }

        String publicRepositoryProp = System.getProperty(PUBLIC_REPOSITORY_PROP_TAG);
        if (publicRepositoryProp != null) {
            return Boolean.valueOf(publicRepositoryProp);
        }

        String publicRepositoryEnv = System.getenv(PUBLIC_REPOSITORY_ENV_TAG);
        if (publicRepositoryEnv != null) {
            return Boolean.valueOf(publicRepositoryEnv);
        }

        return false;
    }

}
