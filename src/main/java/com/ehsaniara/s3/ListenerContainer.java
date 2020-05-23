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

import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.resource.Resource;

import java.io.File;

/**
 * <p>ListenerContainer interface.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
public interface ListenerContainer {

    /**
     * <p>addTransferListener.</p>
     *
     * @param transferListener a {@link org.apache.maven.wagon.events.TransferListener} object.
     */
    void addTransferListener(TransferListener transferListener);

    /**
     * <p>removeTransferListener.</p>
     *
     * @param transferListener a {@link org.apache.maven.wagon.events.TransferListener} object.
     */
    void removeTransferListener(TransferListener transferListener);

    /**
     * <p>hasTransferListener.</p>
     *
     * @param transferListener a {@link org.apache.maven.wagon.events.TransferListener} object.
     * @return a boolean.
     */
    boolean hasTransferListener(TransferListener transferListener);

    /**
     * <p>fireTransferInitiated.</p>
     *
     * @param resource a {@link org.apache.maven.wagon.resource.Resource} object.
     * @param requestType a int.
     */
    void fireTransferInitiated(Resource resource, int requestType);

    /**
     * <p>fireTransferStarted.</p>
     *
     * @param resource a {@link org.apache.maven.wagon.resource.Resource} object.
     * @param requestType a int.
     * @param localFile a {@link java.io.File} object.
     */
    void fireTransferStarted(Resource resource, int requestType, File localFile);

    /**
     * <p>fireTransferProgress.</p>
     *
     * @param resource a {@link org.apache.maven.wagon.resource.Resource} object.
     * @param requestType a int.
     * @param buffer an array of {@link byte} objects.
     * @param length a int.
     */
    void fireTransferProgress(Resource resource, int requestType, byte[] buffer, int length);

    /**
     * <p>fireTransferCompleted.</p>
     *
     * @param resource a {@link org.apache.maven.wagon.resource.Resource} object.
     * @param requestType a int.
     */
    void fireTransferCompleted(Resource resource, int requestType);

    /**
     * <p>fireTransferError.</p>
     *
     * @param resource a {@link org.apache.maven.wagon.resource.Resource} object.
     * @param requestType a int.
     * @param exception a {@link java.lang.Exception} object.
     */
    void fireTransferError(Resource resource, int requestType, Exception exception);

}
