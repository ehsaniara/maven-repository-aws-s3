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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrefixKeysIteratorTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private ListObjectsV2Iterable listObjectsV2Iterable;

    private static final String BUCKET = "test-bucket";
    private static final String PREFIX = "test-prefix/";

    @Test
    void hasNext_withObjects_returnsTrue() {
        S3Object obj1 = S3Object.builder().key("test-prefix/file1.txt").build();
        List<S3Object> objects = Collections.singletonList(obj1);

        when(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Iterable);
        when(listObjectsV2Iterable.contents())
                .thenReturn(() -> objects.iterator());

        PrefixKeysIterator iterator = new PrefixKeysIterator(s3Client, BUCKET, PREFIX);

        assertTrue(iterator.hasNext());
    }

    @Test
    void hasNext_withNoObjects_returnsFalse() {
        List<S3Object> objects = Collections.emptyList();

        when(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Iterable);
        when(listObjectsV2Iterable.contents())
                .thenReturn(() -> objects.iterator());

        PrefixKeysIterator iterator = new PrefixKeysIterator(s3Client, BUCKET, PREFIX);

        assertFalse(iterator.hasNext());
    }

    @Test
    void next_returnsCorrectKey() {
        S3Object obj1 = S3Object.builder().key("test-prefix/file1.txt").build();
        List<S3Object> objects = Collections.singletonList(obj1);

        when(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Iterable);
        when(listObjectsV2Iterable.contents())
                .thenReturn(() -> objects.iterator());

        PrefixKeysIterator iterator = new PrefixKeysIterator(s3Client, BUCKET, PREFIX);

        assertEquals("test-prefix/file1.txt", iterator.next());
    }

    @Test
    void next_withMultipleObjects_iteratesAll() {
        S3Object obj1 = S3Object.builder().key("test-prefix/file1.txt").build();
        S3Object obj2 = S3Object.builder().key("test-prefix/file2.txt").build();
        S3Object obj3 = S3Object.builder().key("test-prefix/file3.txt").build();
        List<S3Object> objects = Arrays.asList(obj1, obj2, obj3);

        when(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Iterable);
        when(listObjectsV2Iterable.contents())
                .thenReturn(() -> objects.iterator());

        PrefixKeysIterator iterator = new PrefixKeysIterator(s3Client, BUCKET, PREFIX);

        assertEquals("test-prefix/file1.txt", iterator.next());
        assertEquals("test-prefix/file2.txt", iterator.next());
        assertEquals("test-prefix/file3.txt", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void next_whenNoMoreElements_returnsNull() {
        List<S3Object> objects = Collections.emptyList();

        when(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Iterable);
        when(listObjectsV2Iterable.contents())
                .thenReturn(() -> objects.iterator());

        PrefixKeysIterator iterator = new PrefixKeysIterator(s3Client, BUCKET, PREFIX);

        assertNull(iterator.next());
    }

    @Test
    void remove_throwsUnsupportedOperationException() {
        List<S3Object> objects = Collections.emptyList();

        when(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Iterable);
        when(listObjectsV2Iterable.contents())
                .thenReturn(() -> objects.iterator());

        PrefixKeysIterator iterator = new PrefixKeysIterator(s3Client, BUCKET, PREFIX);

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void forEachRemaining_throwsUnsupportedOperationException() {
        List<S3Object> objects = Collections.emptyList();

        when(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Iterable);
        when(listObjectsV2Iterable.contents())
                .thenReturn(() -> objects.iterator());

        PrefixKeysIterator iterator = new PrefixKeysIterator(s3Client, BUCKET, PREFIX);

        assertThrows(UnsupportedOperationException.class, () -> iterator.forEachRemaining(s -> {}));
    }
}
