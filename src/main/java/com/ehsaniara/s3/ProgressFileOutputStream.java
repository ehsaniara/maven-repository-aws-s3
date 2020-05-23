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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>ProgressFileOutputStream class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
public final class ProgressFileOutputStream extends FileOutputStream {

    private final Progress progress;

    /**
     * <p>Constructor for ProgressFileOutputStream.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param progress a {@link com.ehsaniara.s3.Progress} object.
     * @throws java.io.FileNotFoundException if any.
     */
    public ProgressFileOutputStream(File file, Progress progress) throws FileNotFoundException {
        super(file);
        this.progress = progress;
    }

    /** {@inheritDoc} */
    @Override
    public void write(int b) throws IOException {
        super.write(b);
        this.progress.progress(new byte[]{(byte) b}, 1);
    }

    /** {@inheritDoc} */
    @Override
    public void write(byte[] bytes) throws IOException {
        super.write(bytes);
        this.progress.progress(bytes, bytes.length);
    }

    /** {@inheritDoc} */
    @Override
    public void write(byte[] byteArray, int off, int len) throws IOException {
        super.write(byteArray, off, len);
        if (off == 0) {
            this.progress.progress(byteArray, len);
        } else {
            byte[] bytes = new byte[len];
            System.arraycopy(byteArray, off, bytes, 0, len);
            this.progress.progress(bytes, len);
        }
    }
}
