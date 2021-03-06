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

import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.FailureProtection;

public class MapCsvLayout<PROP> extends AbstractMapCsvLayout<PROP> {

    private final CsvSetting csvSetting_;

    public MapCsvLayout() {
        csvSetting_ = new DefaultCsvSetting();
    }

    public void setElementSeparator(final char elementSeparator) {
        csvSetting_.setElementSeparator(elementSeparator);
    }

    public void setLineSeparator(final String lineSeparator) {
        csvSetting_.setLineSeparator(lineSeparator);
    }

    public void setQuoteMark(final char quoteMark) {
        csvSetting_.setQuoteMark(quoteMark);
    }

    public void setQuoteMode(final QuoteMode quoteMode) {
        csvSetting_.setQuoteMode(quoteMode);
    }

    protected ElementInOut createElementInOut() {
        final CsvElementInOut a = new CsvElementInOut(csvSetting_);
        a.setLineReaderHandler(getLineReaderHandler());
        return a;
    }

    public RecordInOut<Map<String, PROP>> build() {
        prepareBuild();

        final MapCsvRecordInOut<PROP> obj = new MapCsvRecordInOut<PROP>();
        obj.recordDesc_ = getRecordDesc();
        obj.withHeader_ = isWithHeader();
        obj.elementInOut_ = createElementInOut();
        obj.elementReaderHandler_ = getElementReaderHandler();
        obj.elementEditor_ = getElementEditor();

        return obj;
    }

    protected static class MapCsvRecordInOut<PROP> implements RecordInOut<Map<String, PROP>> {

        private RecordDesc<Map<String, PROP>> recordDesc_;
        private boolean withHeader_;
        private ElementInOut elementInOut_;
        private ElementReaderHandler elementReaderHandler_;
        private ElementEditor elementEditor_;

        @Override
        public RecordReader<Map<String, PROP>> openReader(final Readable readable) {
            if (readable == null) {
                throw new NullPointerException("readable");
            }

            final DefaultRecordReader<Map<String, PROP>> r = new DefaultRecordReader<Map<String, PROP>>(recordDesc_);
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
        public RecordWriter<Map<String, PROP>> openWriter(final Appendable appendable) {
            if (appendable == null) {
                throw new NullPointerException("appendable");
            }

            final DefaultRecordWriter<Map<String, PROP>> w = new DefaultRecordWriter<Map<String, PROP>>(recordDesc_);
            w.setWithHeader(withHeader_);
            w.setElementInOut(elementInOut_);
            // TODO openで例外時にcloseすること
            w.open(appendable);
            return w;
        }

    }
}
