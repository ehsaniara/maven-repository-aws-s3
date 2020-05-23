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

import org.apache.maven.wagon.resource.Resource;

/**
 * <p>ProgressImpl class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
public class ProgressImpl implements Progress {

    private final Resource resource;
    private final int requestType;
    private final ListenerContainer listenerContainer;

    /**
     * <p>Constructor for ProgressImpl.</p>
     *
     * @param resource a {@link org.apache.maven.wagon.resource.Resource} object.
     * @param requestType a int.
     * @param listenerContainer a {@link com.ehsaniara.s3.ListenerContainer} object.
     */
    public ProgressImpl(Resource resource, int requestType, ListenerContainer listenerContainer) {
        this.resource = resource;
        this.requestType = requestType;
        this.listenerContainer = listenerContainer;
    }

    /** {@inheritDoc} */
    @Override
    public void progress(byte[] buffer, int length) {
        listenerContainer.fireTransferProgress(this.resource, this.requestType, buffer, length);
    }
}
