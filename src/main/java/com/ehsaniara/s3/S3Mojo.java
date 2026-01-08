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

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.wagon.authentication.AuthenticationException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <p>S3Mojo class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
@Mojo(name = "s3-download")
public class S3Mojo extends AbstractMojo {

    @Parameter(property = "s3-download.bucket")
    private String bucket;

    @Parameter(property = "s3-download.keys")
    private List<String> keys;

    @Parameter(property = "s3-download.downloadPath")
    private String downloadPath;

    @Parameter(property = "s3-download.region")
    private String region;

    private static final String DIRECTORY_CONTENT_TYPE = "application/x-directory";

    private static final Logger LOGGER = Logger.getLogger(S3Mojo.class.getName());

    /**
     * <p>Constructor for S3Mojo.</p>
     */
    public S3Mojo() {
    }

    /**
     * <p>Constructor for S3Mojo.</p>
     *
     * @param bucket a {@link java.lang.String} object.
     * @param keys a {@link java.util.List} object.
     * @param downloadPath a {@link java.lang.String} object.
     * @param region a {@link java.lang.String} object.
     */
    public S3Mojo(String bucket, List<String> keys, String downloadPath, String region) {
        this.bucket = bucket;
        this.keys = keys;
        this.downloadPath = downloadPath;
        this.region = region;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() throws MojoExecutionException {
        S3Client s3Client;

        try {
            // Path style access is disabled by default in SDK v2
            s3Client = S3Connect.connect(null, region, EndpointProperty.empty(), new PathStyleEnabledProperty("false"));
        } catch (AuthenticationException e) {
            throw new MojoExecutionException(
                    String.format("Unable to authenticate to S3 with the available credentials. Make sure to either define the environment variables or System properties defined in https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html.%n" +
                            "Detail: %s", e.getMessage()),
                    e);
        }

        try {
            if (keys.size() == 1) {
                downloadSingleFile(s3Client, keys.get(0));
                return;
            }

            List<Iterator<String>> prefixKeysIterators = keys.stream()
                    .map(pi -> new PrefixKeysIterator(s3Client, bucket, pi))
                    .collect(Collectors.toList());
            Iterator<String> keyIteratorConcatenated = new KeyIteratorConcatenated(prefixKeysIterators);

            while (keyIteratorConcatenated.hasNext()) {

                String key = keyIteratorConcatenated.next();
                downloadFile(s3Client, key);
            }
        } finally {
            s3Client.close();
        }
    }

    private void downloadSingleFile(S3Client s3Client, String key) {
        File file = new File(downloadPath);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request);
             FileOutputStream fileOutputStream = new FileOutputStream(file)
        ) {
            IOUtils.copy(s3Object, fileOutputStream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not download s3 file");
            e.printStackTrace();
        }
    }

    private void downloadFile(S3Client s3Client, String key) {

        File file = new File(createFullFilePath(key));

        if (file.getParent() != null) {
            file.getParentFile().mkdirs();
        }

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request)) {
            if (isDirectory(s3Object.response())) {
                return;
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                IOUtils.copy(s3Object, fileOutputStream);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not download s3 file");
            e.printStackTrace();
        }
    }

    private String createFullFilePath(String key) {

        String fullPath = downloadPath + "/" + key;
        return fullPath;
    }

    private boolean isDirectory(GetObjectResponse response) {
        return DIRECTORY_CONTENT_TYPE.equals(response.contentType());
    }


}
