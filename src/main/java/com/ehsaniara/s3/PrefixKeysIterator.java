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

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>PrefixKeysIterator class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
public class PrefixKeysIterator implements Iterator<String> {

    private final S3Client s3Client;
    private final String prefix;
    private final String bucket;

    private Iterator<S3Object> s3ObjectIterator;
    private List<String> currentKeys = new ArrayList<>();

    /**
     * <p>Constructor for PrefixKeysIterator.</p>
     *
     * @param s3Client a {@link software.amazon.awssdk.services.s3.S3Client} object.
     * @param bucket a {@link java.lang.String} object.
     * @param prefix a {@link java.lang.String} object.
     */
    public PrefixKeysIterator(S3Client s3Client, String bucket, String prefix) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.prefix = prefix;
        initializeIterator();
    }

    private void initializeIterator() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();

        // SDK v2 paginators handle all pagination automatically
        ListObjectsV2Iterable responses = s3Client.listObjectsV2Paginator(request);
        this.s3ObjectIterator = responses.contents().iterator();
    }

    /** {@inheritDoc} */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void forEachRemaining(Consumer<? super String> action) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        if (!currentKeys.isEmpty()) {
            return true;
        }
        // Prefetch next key if available
        if (s3ObjectIterator.hasNext()) {
            currentKeys.add(s3ObjectIterator.next().key());
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String next() {
        if (!hasNext()) {
            return null;
        }

        return currentKeys.remove(0);
    }

}
