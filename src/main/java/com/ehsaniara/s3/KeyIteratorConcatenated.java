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

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>KeyIteratorConcatenated class.</p>
 *
 * @author jay
 * @version $Id: $Id
 */
public class KeyIteratorConcatenated<T> implements Iterator<T> {

    private final List<Iterator<T>> iterators;

    /**
     * <p>Constructor for KeyIteratorConcatenated.</p>
     *
     * @param iterators a {@link java.util.List} object.
     */
    public KeyIteratorConcatenated(List<Iterator<T>> iterators) {
        this.iterators = iterators;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        if (iterators.size() > 0) {
            if (!iterators.get(0).hasNext()) {
                iterators.remove(0);
                return hasNext();
            }

            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public T next() {
        if (!hasNext()) {
            return null;
        }

        Iterator<T> stringIterator = iterators.get(0);

        return stringIterator.next();
    }

    /** {@inheritDoc} */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        throw new UnsupportedOperationException();
    }
}
