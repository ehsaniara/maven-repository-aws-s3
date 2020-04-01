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

public class EndpointProperty {

    //get it from Environment var
    private static final String S3_ENDPOINT = "S3_ENDPOINT";
    private String endpoint;

    public EndpointProperty(String endpoint) {
        this.endpoint = endpoint;
    }

    public static EndpointProperty empty() {
        return new EndpointProperty(null);
    }

    public boolean isPresent() {
        return Objects.nonNull(endpoint) || System.getProperty(S3_ENDPOINT) != null;
    }

    public String get() {
        if (Objects.nonNull(endpoint)) {
            return endpoint;
        }
        return System.getProperty(S3_ENDPOINT);
    }

}
