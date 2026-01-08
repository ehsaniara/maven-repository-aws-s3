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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageRepoTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private ListObjectsV2Iterable listObjectsV2Iterable;

    private S3StorageRepo repo;

    private static final String BUCKET = "test-bucket";
    private static final String BASE_DIR = "repo/";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        repo = new S3StorageRepo(BUCKET, BASE_DIR, new PublicReadProperty(false));
        // Use reflection to inject mock S3Client
        Field s3ClientField = S3StorageRepo.class.getDeclaredField("s3Client");
        s3ClientField.setAccessible(true);
        s3ClientField.set(repo, s3Client);
    }

    @Test
    void getBucket_returnsCorrectBucket() {
        assertEquals(BUCKET, repo.getBucket());
    }

    @Test
    void getBaseDirectory_returnsCorrectDirectory() {
        assertEquals(BASE_DIR, repo.getBaseDirectory());
    }

    @Test
    void exists_whenObjectExists_returnsTrue() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        boolean exists = repo.exists("some/file.txt");

        assertTrue(exists);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void exists_whenObjectDoesNotExist_returnsFalse() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("Not found").build());

        boolean exists = repo.exists("nonexistent/file.txt");

        assertFalse(exists);
    }

    @Test
    void list_returnsObjectKeys() {
        S3Object obj1 = S3Object.builder().key("repo/file1.txt").build();
        S3Object obj2 = S3Object.builder().key("repo/file2.txt").build();
        List<S3Object> objects = Arrays.asList(obj1, obj2);

        when(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Iterable);
        when(listObjectsV2Iterable.contents())
                .thenReturn(() -> objects.iterator());

        List<String> result = repo.list("");

        assertEquals(2, result.size());
        assertTrue(result.contains("repo/file1.txt"));
        assertTrue(result.contains("repo/file2.txt"));
    }

    @Test
    void newResourceAvailable_whenResourceIsNewer_returnsTrue() throws Exception {
        Instant futureTime = Instant.now().plusSeconds(3600);
        HeadObjectResponse response = HeadObjectResponse.builder()
                .lastModified(futureTime)
                .build();

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(response);

        boolean isNewer = repo.newResourceAvailable("file.txt", 0L);

        assertTrue(isNewer);
    }

    @Test
    void newResourceAvailable_whenResourceIsOlder_returnsFalse() throws Exception {
        Instant pastTime = Instant.ofEpochMilli(1000);
        HeadObjectResponse response = HeadObjectResponse.builder()
                .lastModified(pastTime)
                .build();

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(response);

        boolean isNewer = repo.newResourceAvailable("file.txt", System.currentTimeMillis());

        assertFalse(isNewer);
    }

    @Test
    void disconnect_closesClient() {
        repo.disconnect();

        verify(s3Client).close();
    }

    @Test
    void constructor_withPublicReadTrue_setsProperty() {
        S3StorageRepo publicRepo = new S3StorageRepo(BUCKET, BASE_DIR, new PublicReadProperty(true));

        assertNotNull(publicRepo);
        assertEquals(BUCKET, publicRepo.getBucket());
    }
}
