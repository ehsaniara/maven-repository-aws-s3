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

/**
 * <p>PathStyleEnabledProperty class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
public class PathStyleEnabledProperty {

    private static final String PATH_STYLE_PROP = "S3_PATH_STYLE_ENABLED";
    private String pathStyleEnabled;

    /**
     * <p>Constructor for PathStyleEnabledProperty.</p>
     *
     * @param pathStyleEnabled a {@link java.lang.String} object.
     */
    public PathStyleEnabledProperty(String pathStyleEnabled) {

        this.pathStyleEnabled = pathStyleEnabled;
    }

    /**
     * <p>get.</p>
     *
     * @return a boolean.
     */
    public boolean get() {
        if (pathStyleEnabled != null) {
            return Boolean.parseBoolean(pathStyleEnabled);
        }
        String pathStyleEnv = System.getProperty(PATH_STYLE_PROP);
        if (pathStyleEnv != null) {
            return Boolean.parseBoolean(pathStyleEnv);
        }
        return false;
    }

}
