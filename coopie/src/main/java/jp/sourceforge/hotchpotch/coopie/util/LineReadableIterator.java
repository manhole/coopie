/*
 * Copyright 2010 manhole
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.t2framework.commons.exception.IORuntimeException;

class LineReadableIterator implements Iterator<Line> {

    private final LineReadable reader_;
    private Line next_;

    LineReadableIterator(final LineReadable reader) {
        reader_ = reader;
    }

    @Override
    public boolean hasNext() {
        if (next_ == null) {
            next_ = readLine();
        }
        if (next_ != null) {
            return true;
        }
        return false;
    }

    @Override
    public Line next() {
        if (next_ != null) {
            final Line t = next_;
            next_ = null;
            return t;
        }
        final Line t = readLine();
        if (t == null) {
            throw new NoSuchElementException();
        }
        return t;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    private Line readLine() {
        try {
            return reader_.readLine();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
