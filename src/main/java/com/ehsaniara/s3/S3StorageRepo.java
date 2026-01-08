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

import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * <p>S3StorageRepo class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
@Log
public class S3StorageRepo {

    @Getter
    private final String bucket;
    @Getter
    private final String baseDirectory;

    private final KeyResolver keyResolver = new KeyResolver();

    private S3Client s3Client;
    private PublicReadProperty publicReadProperty;

    /**
     * <p>Constructor for S3StorageRepo.</p>
     *
     * @param bucket a {@link java.lang.String} object.
     * @param baseDirectory a {@link java.lang.String} object.
     * @param publicReadProperty a {@link com.ehsaniara.s3.PublicReadProperty} object.
     */
    public S3StorageRepo(String bucket, String baseDirectory, PublicReadProperty publicReadProperty) {
        this.bucket = bucket;
        this.baseDirectory = baseDirectory;
        this.publicReadProperty = publicReadProperty;
    }

    /**
     * <p>connect.</p>
     *
     * @param authenticationInfo a {@link org.apache.maven.wagon.authentication.AuthenticationInfo} object.
     * @param region a {@link java.lang.String} object.
     * @param endpoint a {@link com.ehsaniara.s3.EndpointProperty} object.
     * @param pathStyle a {@link com.ehsaniara.s3.PathStyleEnabledProperty} object.
     * @throws org.apache.maven.wagon.authentication.AuthenticationException if any.
     */
    public void connect(AuthenticationInfo authenticationInfo, String region, EndpointProperty endpoint, PathStyleEnabledProperty pathStyle) throws AuthenticationException {
        this.s3Client = S3Connect.connect(authenticationInfo, region, endpoint, pathStyle);
    }

    /**
     * <p>copy.</p>
     *
     * @param resourceName a {@link java.lang.String} object.
     * @param destination a {@link java.io.File} object.
     * @param progress a {@link com.ehsaniara.s3.Progress} object.
     * @throws org.apache.maven.wagon.TransferFailedException if any.
     * @throws org.apache.maven.wagon.ResourceDoesNotExistException if any.
     */
    public void copy(String resourceName, File destination, Progress progress) throws TransferFailedException, ResourceDoesNotExistException {

        final String key = resolveKey(resourceName);

        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            final ResponseInputStream<GetObjectResponse> s3Object;
            try {
                s3Object = s3Client.getObject(getRequest);
            } catch (NoSuchKeyException e) {
                throw new ResourceDoesNotExistException("Resource not exist");
            }
            //make sure the folder exists or the outputStream will fail.
            destination.getParentFile().mkdirs();
            //
            try (OutputStream outputStream = new ProgressFileOutputStream(destination, progress);
                 InputStream inputStream = s3Object) {
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (S3Exception | IOException e) {
            log.log(Level.SEVERE, "Could not transfer file", e);
            throw new TransferFailedException("Could not download resource " + key);
        }
    }

    /**
     * <p>put.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param destination a {@link java.lang.String} object.
     * @param progress a {@link com.ehsaniara.s3.Progress} object.
     * @throws org.apache.maven.wagon.TransferFailedException if any.
     */
    public void put(File file, String destination, Progress progress) throws TransferFailedException {

        final String key = resolveKey(destination);

        try {
            try (InputStream inputStream = new ProgressFileInputStream(file, progress)) {
                PutObjectRequest.Builder putRequestBuilder = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentLength(file.length());

                applyPublicRead(putRequestBuilder);

                s3Client.putObject(putRequestBuilder.build(),
                        RequestBody.fromInputStream(inputStream, file.length()));
            }
        } catch (S3Exception | IOException e) {
            log.log(Level.SEVERE, "Could not transfer file ", e);
            throw new TransferFailedException("Could not transfer file " + file.getName());
        }
    }

    /**
     * <p>newResourceAvailable.</p>
     *
     * @param resourceName a {@link java.lang.String} object.
     * @param timeStamp a long.
     * @return a boolean.
     * @throws org.apache.maven.wagon.ResourceDoesNotExistException if any.
     */
    public boolean newResourceAvailable(String resourceName, long timeStamp) throws ResourceDoesNotExistException {

        final String key = resolveKey(resourceName);

        log.log(Level.FINER, String.format("Checking if new key %s exists", key));

        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            HeadObjectResponse response = s3Client.headObject(headRequest);
            long updated = response.lastModified().toEpochMilli();
            return updated > timeStamp;
        } catch (NoSuchKeyException e) {
            log.log(Level.SEVERE, String.format("Could not find %s", key), e);
            throw new ResourceDoesNotExistException("Could not find key " + key);
        }
    }


    /**
     * <p>list.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<String> list(String path) {

        String key = resolveKey(path);

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(key)
                .build();

        List<String> objects = new ArrayList<>();

        // Use paginator for automatic pagination
        s3Client.listObjectsV2Paginator(listRequest)
                .contents()
                .forEach(s3Object -> objects.add(s3Object.key()));

        return objects;
    }

    private void applyPublicRead(PutObjectRequest.Builder putRequestBuilder) {
        if (publicReadProperty.get()) {
            log.info("Public read was set to true");
            putRequestBuilder.acl(ObjectCannedACL.PUBLIC_READ);
        }
    }

    /**
     * <p>exists.</p>
     *
     * @param resourceName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean exists(String resourceName) {

        final String key = resolveKey(resourceName);

        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.headObject(headRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    /**
     * <p>disconnect.</p>
     */
    public void disconnect() {
        if (s3Client != null) {
            s3Client.close();
        }
        s3Client = null;
    }

    private String resolveKey(String path) {
        return keyResolver.resolve(baseDirectory, path);
    }


}
