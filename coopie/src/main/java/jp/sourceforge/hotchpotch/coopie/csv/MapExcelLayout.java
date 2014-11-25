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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class MapExcelLayout<PROP> extends AbstractMapCsvLayout<PROP> {

    public ExcelInOut<Map<String, PROP>> build() {
        prepareBuild();

        final MapExcelInOut<PROP> obj = new MapExcelInOut<PROP>();
        obj.recordDesc_ = getRecordDesc();
        obj.withHeader_ = isWithHeader();
        obj.elementReaderHandler_ = getElementReaderHandler();
        return obj;
    }

    public static class MapExcelInOut<PROP> implements ExcelInOut<Map<String, PROP>> {

        private RecordDesc<Map<String, PROP>> recordDesc_;
        private boolean withHeader_;
        private ElementReaderHandler elementReaderHandler_;

        @Override
        public RecordReader<Map<String, PROP>> openReader(final InputStream is) {
            if (is == null) {
                throw new NullPointerException("is");
            }

            final DefaultExcelReader<Map<String, PROP>> r = new DefaultExcelReader<Map<String, PROP>>(recordDesc_);
            r.setWithHeader(withHeader_);
            r.setElementReaderHandler(elementReaderHandler_);

            // TODO openで例外時にcloseすること
            r.open(is);
            return r;
        }

        @Override
        public RecordWriter<Map<String, PROP>> openWriter(final OutputStream os) {
            if (os == null) {
                throw new NullPointerException("os");
            }

            final DefaultExcelWriter<Map<String, PROP>> w = new DefaultExcelWriter<Map<String, PROP>>(recordDesc_);
            w.setWithHeader(withHeader_);
            // TODO openで例外時にcloseすること
            w.open(os);
            return w;
        }

    }
}
