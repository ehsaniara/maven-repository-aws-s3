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

import com.amazonaws.services.s3.model.AmazonS3Exception;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.maven.wagon.PathUtils;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.resource.Resource;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Setter
@Getter
public class S3StorageWagon extends AbstractStorageWagon {

    private S3StorageRepo s3StorageRepo;
    private final KeyResolver keyResolver = new KeyResolver();

    private String region;
    private Boolean publicRepository;

    private static final Logger LOGGER = Logger.getLogger(S3StorageWagon.class.getName());
    private String endpoint;
    private String pathStyleEnabled;

    @Override
    public void get(String resourceName, File file) throws TransferFailedException, ResourceDoesNotExistException {

        Resource resource = new Resource(resourceName);
        listenerContainer.fireTransferInitiated(resource, TransferEvent.REQUEST_GET);
        listenerContainer.fireTransferStarted(resource, TransferEvent.REQUEST_GET, file);

        final Progress progress = new ProgressImpl(resource, TransferEvent.REQUEST_GET, listenerContainer);

        try {
            s3StorageRepo.copy(resourceName, file, progress);
            listenerContainer.fireTransferCompleted(resource, TransferEvent.REQUEST_GET);
        } catch (Exception e) {
            listenerContainer.fireTransferError(resource, TransferEvent.REQUEST_GET, e);
            throw e;
        }
    }


    @Override
    public List<String> getFileList(String s) throws TransferFailedException, ResourceDoesNotExistException {
        try {
            List<String> list = s3StorageRepo.list(s);
            list = convertS3ListToMavenFileList(list, s);
            if (list.isEmpty()) {
                throw new ResourceDoesNotExistException(s);
            }
            return list;
        } catch (AmazonS3Exception e) {
            throw new TransferFailedException("Could not fetch objects for prefix " + s);
        }
    }

    @Override
    public void put(File file, String resourceName) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {

        Resource resource = new Resource(resourceName);

        LOGGER.log(Level.FINER, String.format("Uploading file %s to %s", file.getAbsolutePath(), resourceName));

        listenerContainer.fireTransferInitiated(resource, TransferEvent.REQUEST_PUT);
        listenerContainer.fireTransferStarted(resource, TransferEvent.REQUEST_PUT, file);
        final Progress progress = new ProgressImpl(resource, TransferEvent.REQUEST_PUT, listenerContainer);

        try {
            s3StorageRepo.put(file, resourceName, progress);
            listenerContainer.fireTransferCompleted(resource, TransferEvent.REQUEST_PUT);
        } catch (TransferFailedException e) {
            listenerContainer.fireTransferError(resource, TransferEvent.REQUEST_PUT, e);
            throw e;
        }
    }

    @Override
    public boolean getIfNewer(String resourceName, File file, long timeStamp) throws TransferFailedException, ResourceDoesNotExistException {

        if (s3StorageRepo.newResourceAvailable(resourceName, timeStamp)) {
            get(resourceName, file);
            return true;
        }

        return false;
    }

    @Override
    public void putDirectory(File source, String destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        Collection<File> allFiles = FileUtils.listFiles(source, null, true);
        String relativeDestination = destination;
        // the initial deleting.
        if (destination != null && destination.startsWith(".")) {
            relativeDestination = destination.length() == 1 ? "" : destination.substring(1);
        }
        for (File file : allFiles) {
            String relativePath = PathUtils.toRelative(source, file.getAbsolutePath());
            put(file, relativeDestination + "/" + relativePath);
        }
    }

    @Override
    public boolean resourceExists(String resourceName) {
        return s3StorageRepo.exists(resourceName);
    }


    private List<String> convertS3ListToMavenFileList(List<String> list, String path) {
        String prefix = keyResolver.resolve(s3StorageRepo.getBaseDirectory(), path);
        Set<String> folders = new HashSet<>();
        List<String> result = list.stream().map(key -> {
            String filePath = key;
            // deleting the prefix from the object path
            if (prefix != null && prefix.length() > 0)
                filePath = key.substring(prefix.length() + 1);

            extractFolders(folders, filePath);
            return filePath;
        }).collect(Collectors.toList());
        result.addAll(folders);
        return result;
    }

    private void extractFolders(Set<String> folders, String filePath) {
        if (filePath.contains("/")) {
            String folder = filePath.substring(0, filePath.lastIndexOf('/'));
            folders.add(folder + '/');
            if (folder.contains("/")) {
                extractFolders(folders, folder);
            }
        } else {
            folders.add(filePath);
        }
    }

    @Override
    public void connect(Repository repository, AuthenticationInfo authenticationInfo, ProxyInfoProvider proxyInfoProvider) throws AuthenticationException {

        this.repository = repository;
        this.sessionListenerContainer.fireSessionOpening();

        final String bucket = repository.getHost();

        StringBuilder stringBuilder = new StringBuilder(repository.getBasedir()).deleteCharAt(0);
        if ((stringBuilder.length() > 0) //
                && (stringBuilder.charAt(stringBuilder.length() - 1) != '/')) {
            stringBuilder.append('/');
        }

        final String directory = stringBuilder.toString();

        LOGGER.log(Level.FINER, String.format("Opening connection for bucket %s and directory %s", bucket, directory));
        s3StorageRepo = new S3StorageRepo(bucket, directory, new PublicReadProperty(publicRepository));
        s3StorageRepo.connect(authenticationInfo, region, new EndpointProperty(endpoint), new PathStyleEnabledProperty(pathStyleEnabled));

        sessionListenerContainer.fireSessionLoggedIn();
        sessionListenerContainer.fireSessionOpened();
    }

    @Override
    public void disconnect() {
        sessionListenerContainer.fireSessionDisconnecting();
        s3StorageRepo.disconnect();
        sessionListenerContainer.fireSessionLoggedOff();
        sessionListenerContainer.fireSessionDisconnected();
    }

}
