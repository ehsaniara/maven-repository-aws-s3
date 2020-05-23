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
import java.io.FileInputStream;
import java.io.IOException;

/**
 * <p>ProgressFileInputStream class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
public final class ProgressFileInputStream extends FileInputStream {

    private final Progress progress;
    private long byteLeft;

    /**
     * <p>Constructor for ProgressFileInputStream.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param progress a {@link com.ehsaniara.s3.Progress} object.
     * @throws java.io.IOException if any.
     */
    public ProgressFileInputStream(File file, Progress progress) throws IOException {
        super(file);
        this.progress = progress;
        resetByteLeft();
    }

    private void resetByteLeft() throws IOException {
        byteLeft = this.getChannel().size();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        resetByteLeft();
    }

    /** {@inheritDoc} */
    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b != -1) {
            this.progress.progress(new byte[]{(byte) b}, 1);
            byteLeft--;
        }
        //I try to read but it was the end of the stream so nothing to report
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] b) throws IOException {
        int count = super.read(b);
        if (count != -1) {
            this.progress.progress(b, b.length);
            byteLeft -= b.length;
        } else {//end of the stream
            this.progress.progress(b, Math.toIntExact(byteLeft));
        }
        return count;
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = super.read(b, off, len);
        if (off == 0) {
            if (count != -1) {
                this.progress.progress(b, count);
                byteLeft -= count;
            } else {//end of the stream
                this.progress.progress(b, Math.toIntExact(byteLeft));
            }
        } else {
            if (count != -1) {
                byte[] bytes = new byte[count];
                System.arraycopy(b, off, bytes, 0, count);
                this.progress.progress(bytes, len);
                byteLeft -= count;
            } else {//end of the stream
                byte[] bytes = new byte[Math.toIntExact(byteLeft)];
                System.arraycopy(b, off, bytes, 0, Math.toIntExact(byteLeft));
                this.progress.progress(b, Math.toIntExact(byteLeft));
            }
        }
        return count;
    }
}
