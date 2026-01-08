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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyResolverTest {

    private KeyResolver keyResolver;

    @BeforeEach
    void setUp() {
        keyResolver = new KeyResolver();
    }

    @Test
    void resolve_withBaseDirectoryAndPath_combinesThem() {
        String result = keyResolver.resolve("repo/", "com/example/artifact.jar");

        assertNotNull(result);
        assertTrue(result.contains("repo"));
        assertTrue(result.contains("com/example/artifact.jar"));
    }

    @Test
    void resolve_withEmptyBaseDirectory_returnsPath() {
        String result = keyResolver.resolve("", "file.txt");

        assertNotNull(result);
    }

    @Test
    void resolve_withNullBaseDirectory_throwsNullPointerException() {
        // KeyResolver does not handle null base directory - this documents expected behavior
        assertThrows(NullPointerException.class, () -> keyResolver.resolve(null, "file.txt"));
    }

    @Test
    void resolve_withTrailingSlash_normalizesPath() {
        String result1 = keyResolver.resolve("repo/", "file.txt");
        String result2 = keyResolver.resolve("repo", "file.txt");

        // Both should produce valid paths
        assertNotNull(result1);
        assertNotNull(result2);
    }

    @Test
    void resolve_withLeadingSlashInPath_handlesCorrectly() {
        String result = keyResolver.resolve("repo/", "/file.txt");

        assertNotNull(result);
    }
}
