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

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.Closable;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;

public class AbstractRecordWriter<BEAN> implements Closable, RecordWriter<BEAN> {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    /**
     * RecordWriter close時に、Writerを一緒にcloseする場合はtrue。
     */
    private boolean closeWriter_ = true;

    private boolean firstRecord_ = true;

    private ElementWriter elementWriter_;
    private boolean withHeader_;
    private boolean writtenHeader_;

    private RecordDesc<BEAN> recordDesc_;

    public AbstractRecordWriter(final RecordDesc<BEAN> recordDesc) {
        recordDesc_ = recordDesc;
    }

    /*
     * 1レコード目を出力するときに、このメソッドが呼ばれる。
     */
    protected void writeHeader(final BEAN bean) {
        final String[] line = recordDesc_.getHeaderValues();
        elementWriter_.writeRecord(line);
    }

    @Override
    public void write(final BEAN bean) {
        if (firstRecord_) {
            firstRecord_ = false;
            recordDesc_ = recordDesc_.setupByBean(bean);
        }
        if (withHeader_ && !writtenHeader_) {
            writeHeader(bean);
            writtenHeader_ = true;
        }
        final String[] line = recordDesc_.getValues(bean);
        elementWriter_.writeRecord(line);
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        if (closeWriter_) {
            CloseableUtil.closeNoException(elementWriter_);
            elementWriter_ = null;
        }
    }

    protected void setClosed(final boolean closed) {
        closed_ = closed;
    }

    public void setCloseWriter(final boolean closeWriter) {
        closeWriter_ = closeWriter;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    protected void setElementWriter(final ElementWriter elementWriter) {
        elementWriter_ = elementWriter;
    }

}
