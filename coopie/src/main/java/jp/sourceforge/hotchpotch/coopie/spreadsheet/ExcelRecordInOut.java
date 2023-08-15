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

package jp.sourceforge.hotchpotch.coopie.spreadsheet;

import java.io.InputStream;
import java.io.OutputStream;

import jp.sourceforge.hotchpotch.coopie.csv.ElementReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author manhole
 */
class ExcelRecordInOut<BEAN> implements ExcelInOut<BEAN> {

    private RecordDesc<BEAN> recordDesc_;
    private boolean withHeader_;
    private ElementReaderHandler elementReaderHandler_;
    private DefaultExcelWriter.WriteEditor writeEditor_;

    @Override
    public RecordReader<BEAN> openReader(final InputStream is) {
        if (is == null) {
            throw new NullPointerException("is");
        }

        final DefaultExcelReader<BEAN> r = new DefaultExcelReader<>(recordDesc_);
        r.setWithHeader(withHeader_);
        r.setElementReaderHandler(elementReaderHandler_);

        // TODO openで例外時にcloseすること
        r.open(is);
        return r;
    }

    @Override
    public RecordWriter<BEAN> openWriter(final OutputStream os) {
        if (os == null) {
            throw new NullPointerException("os");
        }

        final DefaultExcelWriter<BEAN> w = new DefaultExcelWriter<>(recordDesc_);
        w.setWithHeader(withHeader_);
        if (writeEditor_ != null) {
            w.setWriteEditor(writeEditor_);
        }
        // TODO openで例外時にcloseすること
        w.open(os);
        return w;
    }

    public RecordReader<BEAN> openSheetReader(final Sheet sheet) {
        final DefaultExcelReader<BEAN> r = new DefaultExcelReader<>(recordDesc_);
        r.setWithHeader(withHeader_);
        r.setElementReaderHandler(elementReaderHandler_);

        // TODO openで例外時にcloseすること
        r.openSheetReader(sheet);
        return r;
    }

    public RecordWriter<BEAN> openSheetWriter(final Workbook workbook, final Sheet sheet) {
        final DefaultExcelWriter<BEAN> w = new DefaultExcelWriter<>(recordDesc_);
        w.setWithHeader(withHeader_);
        // TODO openで例外時にcloseすること
        w.openSheetWriter(workbook, sheet);
        return w;
    }

    public void setRecordDesc(final RecordDesc<BEAN> recordDesc) {
        recordDesc_ = recordDesc;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    public void setElementReaderHandler(final ElementReaderHandler elementReaderHandler) {
        elementReaderHandler_ = elementReaderHandler;
    }

    public void setWriteEditor(final DefaultExcelWriter.WriteEditor writeEditor) {
        writeEditor_ = writeEditor;
    }

}
