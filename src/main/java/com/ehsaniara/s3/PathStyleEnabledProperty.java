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

public class PathStyleEnabledProperty {

    private static final String PATH_STYLE_PROP = "S3_PATH_STYLE_ENABLED";
    private String pathStyleEnabled;

    public PathStyleEnabledProperty(String pathStyleEnabled) {

        this.pathStyleEnabled = pathStyleEnabled;
    }

    public boolean get() {
        if (pathStyleEnabled != null) {
            return Boolean.valueOf(pathStyleEnabled);
        }
        String pathStyleEnv = System.getProperty(PATH_STYLE_PROP);
        if (pathStyleEnv != null) {
            return Boolean.valueOf(pathStyleEnv);
        }
        return false;
    }

}
