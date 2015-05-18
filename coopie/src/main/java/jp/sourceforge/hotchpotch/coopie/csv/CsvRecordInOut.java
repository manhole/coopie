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

import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.FailureProtection;

/**
 * @author manhole
 */
class CsvRecordInOut<BEAN> implements RecordInOut<BEAN> {

    private RecordDesc<BEAN> recordDesc_;
    private boolean withHeader_;
    private ElementInOut elementInOut_;
    private ElementReaderHandler elementReaderHandler_;
    private ElementEditor elementEditor_;

    @Override
    public RecordReader<BEAN> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        final DefaultRecordReader<BEAN> r = new DefaultRecordReader<BEAN>(recordDesc_);
        r.setWithHeader(withHeader_);
        r.setElementInOut(elementInOut_);
        r.setElementReaderHandler(elementReaderHandler_);
        r.setElementEditor(elementEditor_);
        new FailureProtection<RuntimeException>() {

            @Override
            protected void protect() {
                r.open(readable);
            }

            @Override
            protected void rescue() {
                CloseableUtil.closeNoException(r);
            }

        }.execute();
        return r;
    }

    @Override
    public RecordWriter<BEAN> openWriter(final Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        final DefaultRecordWriter<BEAN> w = new DefaultRecordWriter<BEAN>(recordDesc_);
        w.setWithHeader(withHeader_);
        w.setElementInOut(elementInOut_);
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    public void setRecordDesc(final RecordDesc<BEAN> recordDesc) {
        recordDesc_ = recordDesc;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    public void setElementInOut(final ElementInOut elementInOut) {
        elementInOut_ = elementInOut;
    }

    public void setElementReaderHandler(final ElementReaderHandler elementReaderHandler) {
        elementReaderHandler_ = elementReaderHandler;
    }

    public void setElementEditor(final ElementEditor elementEditor) {
        elementEditor_ = elementEditor;
    }

}
