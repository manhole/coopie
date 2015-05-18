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

import java.io.IOException;
import java.io.OutputStream;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractRecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.ElementWriter;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class DefaultExcelWriter<BEAN> extends AbstractRecordWriter<BEAN> {

    private WriteEditor writeEditor_ = DefaultWriteEditor.getInstance();

    public DefaultExcelWriter(final RecordDesc<BEAN> recordDesc) {
        super(recordDesc);
    }

    public void open(final OutputStream os) {
        final PoiWriter w = new PoiWriter(os);
        w.setWriteEditor(writeEditor_);
        w.open();
        setElementWriter(w);
        setClosed(false);
    }

    public void openSheetWriter(final Workbook workbook, final Sheet sheet) {
        final PoiSheetWriter w = new PoiSheetWriter(workbook, sheet);
        w.setWriteEditor(writeEditor_);
        w.open();
        setElementWriter(w);
        setClosed(false);
    }

    public void setWriteEditor(final WriteEditor writeEditor) {
        writeEditor_ = writeEditor;
    }

    static class PoiWriter implements ElementWriter {

        private final OutputStream os_;
        private boolean closed_ = true;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);

        private Workbook workbook_;
        private PoiSheetWriter sheetWriter_;
        private WriteEditor writeEditor_ = DefaultWriteEditor.getInstance();

        public PoiWriter(final OutputStream os) {
            os_ = os;
        }

        public void open() {
            // TODO とりあえずxls固定のまま
            workbook_ = new HSSFWorkbook();
            final Sheet sheet = workbook_.createSheet();
            sheetWriter_ = new PoiSheetWriter(workbook_, sheet);
            sheetWriter_.setWriteEditor(writeEditor_);
            sheetWriter_.open();
            closed_ = false;
        }

        @Override
        public void writeRecord(final String[] line) {
            sheetWriter_.writeRecord(line);
        }

        @Override
        public boolean isClosed() {
            return closed_;
        }

        @Override
        public void close() throws IOException {
            closed_ = true;
            CloseableUtil.closeNoException(sheetWriter_);
            if (workbook_ != null) {
                workbook_.write(os_);
                workbook_ = null;
                CloseableUtil.closeNoException(os_);
            }
        }

        public void setWriteEditor(final WriteEditor writeEditor) {
            writeEditor_ = writeEditor;
        }

    }

    public static class PoiSheetWriter implements ElementWriter {

        private boolean closed_ = true;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);

        private final Workbook workbook_;
        private Sheet sheet_;
        private int rowNum_;
        private WriteEditor writeEditor_ = DefaultWriteEditor.getInstance();

        public PoiSheetWriter(final Workbook workbook, final Sheet sheet) {
            workbook_ = workbook;
            sheet_ = sheet;
        }

        public void open() {
            writeEditor_.begin(workbook_, sheet_);
            closed_ = false;
        }

        @Override
        public void writeRecord(final String[] line) {
            final Row row = writeEditor_.createRow(rowNum_);
            rowNum_++;

            for (int i = 0; i < line.length; i++) {
                final String s = line[i];
                final Cell cell = writeEditor_.createCell(row, (short) i);
                writeEditor_.setCellValue(cell, s);
            }
        }

        @Override
        public boolean isClosed() {
            return closed_;
        }

        @Override
        public void close() throws IOException {
            closed_ = true;
            sheet_ = null;
        }

        public void setWriteEditor(final WriteEditor writeEditor) {
            writeEditor_ = writeEditor;
        }

    }

    public interface WriteEditor {

        Row createRow(int rowNum);

        void begin(Workbook workbook, Sheet sheet);

        void setCellValue(Cell cell, String value);

        Cell createCell(Row row, int colNum);

    }

    public static class DefaultWriteEditor implements WriteEditor {

        private static final WriteEditor INSTANCE = new DefaultWriteEditor();

        private Workbook workbook_;
        private Sheet sheet_;

        public static WriteEditor getInstance() {
            return INSTANCE;
        }

        @Override
        public void begin(final Workbook workbook, final Sheet sheet) {
            workbook_ = workbook;
            sheet_ = sheet;
        }

        @Override
        public Row createRow(final int rowNum) {
            return sheet_.createRow(rowNum);
        }

        @Override
        public Cell createCell(final Row row, final int colNum) {
            return row.createCell(colNum);
        }

        @Override
        public void setCellValue(final Cell cell, final String value) {
            cell.setCellValue(value);
        }

        protected Workbook getWorkbook() {
            return workbook_;
        }

        protected Sheet getSheet() {
            return sheet_;
        }

    }

    public static class WriteEditorWrapper implements WriteEditor {

        private final WriteEditor delegate_;

        public WriteEditorWrapper(final WriteEditor delegate) {
            delegate_ = delegate;
        }

        @Override
        public Row createRow(final int rowNum) {
            return delegate_.createRow(rowNum);
        }

        @Override
        public void begin(final Workbook workbook, final Sheet sheet) {
            delegate_.begin(workbook, sheet);
        }

        @Override
        public void setCellValue(final Cell cell, final String value) {
            delegate_.setCellValue(cell, value);
        }

        @Override
        public Cell createCell(final Row row, final int colNum) {
            return delegate_.createCell(row, colNum);
        }
    }

}
