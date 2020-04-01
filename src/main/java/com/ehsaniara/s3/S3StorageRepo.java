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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Log
public class S3StorageRepo {

    @Getter
    private final String bucket;
    @Getter
    private final String baseDirectory;

    private final KeyResolver keyResolver = new KeyResolver();

    private AmazonS3 amazonS3;
    private PublicReadProperty publicReadProperty;

    public S3StorageRepo(String bucket, String baseDirectory, PublicReadProperty publicReadProperty) {
        this.bucket = bucket;
        this.baseDirectory = baseDirectory;
        this.publicReadProperty = publicReadProperty;
    }

    public void connect(AuthenticationInfo authenticationInfo, String region, EndpointProperty endpoint, PathStyleEnabledProperty pathStyle) throws AuthenticationException {
        this.amazonS3 = S3Connect.connect(authenticationInfo, region, endpoint, pathStyle);
    }

    public void copy(String resourceName, File destination, Progress progress) throws TransferFailedException, ResourceDoesNotExistException {

        final String key = resolveKey(resourceName);

        try {

            final S3Object s3Object;
            try {
                s3Object = amazonS3.getObject(bucket, key);
            } catch (AmazonS3Exception e) {
                throw new ResourceDoesNotExistException("Resource not exist");
            }
            //make sure the folder exists or the outputStream will fail.
            destination.getParentFile().mkdirs();
            //
            try (OutputStream outputStream = new ProgressFileOutputStream(destination, progress);
                 InputStream inputStream = s3Object.getObjectContent()) {
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (AmazonS3Exception | IOException e) {
            log.log(Level.SEVERE, "Could not transfer file", e);
            throw new TransferFailedException("Could not download resource " + key);
        }
    }

    public void put(File file, String destination, Progress progress) throws TransferFailedException {

        final String key = resolveKey(destination);

        try {
            try (InputStream inputStream = new ProgressFileInputStream(file, progress)) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, inputStream, createContentLengthMetadata(file));
                applyPublicRead(putObjectRequest);
                amazonS3.putObject(putObjectRequest);
            }
        } catch (AmazonS3Exception | IOException e) {
            log.log(Level.SEVERE, "Could not transfer file ", e);
            throw new TransferFailedException("Could not transfer file " + file.getName());
        }
    }

    private ObjectMetadata createContentLengthMetadata(File file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());
        return metadata;
    }

    public boolean newResourceAvailable(String resourceName, long timeStamp) throws ResourceDoesNotExistException {

        final String key = resolveKey(resourceName);

        log.log(Level.FINER, String.format("Checking if new key %s exists", key));

        try {
            ObjectMetadata objectMetadata = amazonS3.getObjectMetadata(bucket, key);

            long updated = objectMetadata.getLastModified().getTime();
            return updated > timeStamp;
        } catch (AmazonS3Exception e) {
            log.log(Level.SEVERE, String.format("Could not find %s", key), e);
            throw new ResourceDoesNotExistException("Could not find key " + key);
        }
    }


    public List<String> list(String path) {

        String key = resolveKey(path);

        ObjectListing objectListing = amazonS3.listObjects(new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(key));
        List<String> objects = new ArrayList<>();
        retrieveAllObjects(objectListing, objects);
        return objects;
    }

    private void applyPublicRead(PutObjectRequest putObjectRequest) {
        if (publicReadProperty.get()) {
            log.info("Public read was set to true");
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        }
    }

    private void retrieveAllObjects(ObjectListing objectListing, List<String> objects) {

        objectListing.getObjectSummaries().forEach(os -> objects.add(os.getKey()));

        if (objectListing.isTruncated()) {
            ObjectListing nextObjectListing = amazonS3.listNextBatchOfObjects(objectListing);
            retrieveAllObjects(nextObjectListing, objects);
        }
    }

    public boolean exists(String resourceName) {

        final String key = resolveKey(resourceName);

        try {
            amazonS3.getObjectMetadata(bucket, key);
            return true;
        } catch (AmazonS3Exception e) {
            return false;
        }
    }

    public void disconnect() {
        amazonS3 = null;
    }

    private String resolveKey(String path) {
        return keyResolver.resolve(baseDirectory, path);
    }


}
