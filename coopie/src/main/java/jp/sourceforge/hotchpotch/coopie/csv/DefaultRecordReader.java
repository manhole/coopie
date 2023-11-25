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

package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DefaultRecordReader<BEAN> extends AbstractRecordReader<BEAN> {

    private ElementInOut elementInOut_;

    public DefaultRecordReader(final RecordDesc<BEAN> recordDesc) {
        super(recordDesc);
    }

    public void setElementInOut(final ElementInOut elementInOut) {
        elementInOut_ = elementInOut;
    }

    public void open(final Readable readable) {
        final ElementReader elementReader = elementInOut_.openReader(readable);
        setElementReader(elementReader);
        setClosed(false);

        setupByHeader();
    }

    @Override
    public Iterator<BEAN> iterator() {
        return new RecoardReaderIterator<>(this);
    }

    private static class RecoardReaderIterator<BEAN> implements Iterator<BEAN> {

        private final DefaultRecordReader<BEAN> reader_;
        private BEAN next_;

        public RecoardReaderIterator(final DefaultRecordReader<BEAN> reader) {
            reader_ = reader;
        }

        @Override
        public boolean hasNext() {
            if (next_ == null) {
                next_ = readNext();
            }
            if (next_ != null) {
                return true;
            }
            return false;
        }

        @Override
        public BEAN next() {
            if (next_ != null) {
                final BEAN t = next_;
                next_ = null;
                return t;
            }
            final BEAN t = readNext();
            if (t == null) {
                throw new NoSuchElementException();
            }
            return t;
        }

        private BEAN readNext() {
            if (reader_.hasNext()) {
                return reader_.read();
            }
            return null;
        }

    }

}
