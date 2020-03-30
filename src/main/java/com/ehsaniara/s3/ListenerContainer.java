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

public interface ListenerContainer {

    void addTransferListener(TransferListener transferListener);

    void removeTransferListener(TransferListener transferListener);

    boolean hasTransferListener(TransferListener transferListener);

    void fireTransferInitiated(Resource resource, int requestType);

    void fireTransferStarted(Resource resource, int requestType, File localFile);

    void fireTransferProgress(Resource resource, int requestType, byte[] buffer, int length);

    void fireTransferCompleted(Resource resource, int requestType);

    void fireTransferError(Resource resource, int requestType, Exception exception);

}
