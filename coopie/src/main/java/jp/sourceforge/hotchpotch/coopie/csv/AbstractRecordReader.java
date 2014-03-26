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
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.Closable;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;

import org.slf4j.Logger;

public abstract class AbstractRecordReader<BEAN> implements Closable, RecordReader<BEAN> {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * RecordReader close時に、Readerを一緒にcloseする場合はtrue。
     */
    private boolean closeReader_ = true;

    private RecordDesc<BEAN> recordDesc_;
    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private ElementReader elementReader_;
    private ElementReaderHandler elementReaderHandler_ = DefaultElementReaderHandler.getInstance();

    private boolean withHeader_;

    private Boolean hasNext_;
    private String[] nextLine_;
    private int recordNo_;

    private ElementEditor elementEditor_;

    public AbstractRecordReader(final RecordDesc<BEAN> recordDesc) {
        recordDesc_ = recordDesc;
    }

    @Override
    public void read(final BEAN bean) {
        if (!hasNext()) {
            throw new NoSuchElementException("no element");
        }
        hasNext_ = null;

        if (nextLine_ == null) {
            throw new AssertionError();
        }

        final String[] line = nextLine_;
        recordDesc_.setValues(bean, line);
        recordNo_++;
    }

    @Override
    public BEAN read() {
        final BEAN bean = newInstance();
        read(bean);
        return bean;
    }

    protected BEAN newInstance() {
        return recordDesc_.newInstance();
    }

    protected String[] readLine() {
        final String[] elements = elementReaderHandler_.readRecord(elementReader_);
        if (elements != null && elementEditor_ != null) {
            for (int i = 0; i < elements.length; i++) {
                final String elem = elements[i];
                final String edited = elementEditor_.edit(elem);
                elements[i] = edited;
            }
        }
        return elements;
    }

    @Override
    public boolean hasNext() {
        if (hasNext_ != null) {
            return hasNext_.booleanValue();
        }
        nextLine_ = readLine();

        if (nextLine_ == null) {
            hasNext_ = Boolean.FALSE;
        } else {
            hasNext_ = Boolean.TRUE;
        }
        return hasNext_.booleanValue();
    }

    protected void setupByHeader() {
        if (withHeader_) {
            final String[] header = readLine();
            if (header == null) {
                logger.debug("header is null");
                return;
            }
            recordDesc_ = recordDesc_.setupByHeader(header);
        } else {
            /*
             * ヘッダなしの場合は、列順が指定されていないとダメ。
             * JavaBeansのプロパティ情報は順序が不定なため。
             */
            if (OrderSpecified.SPECIFIED != recordDesc_.getOrderSpecified()) {
                if (readLine() == null) {
                    /*
                     * 本来はエラーだが、空ファイルに限ってはOKとしておく。
                     * (将来エラーに変更するかも)
                     */
                    logger.debug("header is null");
                    return;
                }
                throw new IllegalStateException("no column order set");
            }
        }
    }

    @Override
    public int getRecordNumber() {
        return recordNo_;
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        if (closeReader_) {
            CloseableUtil.closeNoException(elementReader_);
            elementReader_ = null;
        }
    }

    protected void setClosed(final boolean closed) {
        closed_ = closed;
    }

    public void setCloseReader(final boolean closeReader) {
        closeReader_ = closeReader;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    protected ElementReaderHandler getElementReaderHandler() {
        return elementReaderHandler_;
    }

    public void setElementReaderHandler(final ElementReaderHandler elementReaderHandler) {
        elementReaderHandler_ = elementReaderHandler;
    }

    public void setElementEditor(final ElementEditor elementEditor) {
        elementEditor_ = elementEditor;
    }

    protected ElementReader getElementReader() {
        return elementReader_;
    }

    protected void setElementReader(final ElementReader elementReader) {
        elementReader_ = elementReader;
    }

}
