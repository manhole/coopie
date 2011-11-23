package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.OutputStream;

import jp.sourceforge.hotchpotch.coopie.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.IOUtil;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class DefaultExcelWriter<T> extends AbstractRecordWriter<T> {

    private WriteEditor writeEditor = DefaultWriteEditor.getInstance();

    public DefaultExcelWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void open(final OutputStream os) {
        final PoiWriter w = new PoiWriter(os);
        w.setWriteEditor(writeEditor);
        w.open();
        elementWriter = w;
        closed = false;
    }

    public void openSheetWriter(final HSSFWorkbook workbook,
            final HSSFSheet sheet) {
        final PoiSheetWriter w = new PoiSheetWriter(workbook, sheet);
        w.setWriteEditor(writeEditor);
        w.open();
        elementWriter = w;
        closed = false;
    }

    public void setWriteEditor(final WriteEditor writeEditor) {
        this.writeEditor = writeEditor;
    }

    static class PoiWriter implements ElementWriter {

        private final OutputStream os;
        private boolean closed = true;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian = new ClosingGuardian(this);

        private HSSFWorkbook workbook;
        private PoiSheetWriter sheetWriter;
        private WriteEditor writeEditor = DefaultWriteEditor.getInstance();

        public PoiWriter(final OutputStream os) {
            this.os = os;
        }

        public void open() {
            workbook = new HSSFWorkbook();
            final HSSFSheet sheet = workbook.createSheet();
            sheetWriter = new PoiSheetWriter(workbook, sheet);
            sheetWriter.setWriteEditor(writeEditor);
            sheetWriter.open();
            closed = false;
        }

        @Override
        public void writeRecord(final String[] line) {
            sheetWriter.writeRecord(line);
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            IOUtil.closeNoException(sheetWriter);
            if (workbook != null) {
                workbook.write(os);
                workbook = null;
                IOUtil.closeNoException(os);
            }
        }

        public void setWriteEditor(final WriteEditor writeEditor) {
            this.writeEditor = writeEditor;
        }

    }

    static class PoiSheetWriter implements ElementWriter {

        private boolean closed = true;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian = new ClosingGuardian(this);

        private final HSSFWorkbook workbook;
        private HSSFSheet sheet;
        private int rowNum;
        private WriteEditor writeEditor = DefaultWriteEditor.getInstance();

        public PoiSheetWriter(final HSSFWorkbook workbook, final HSSFSheet sheet) {
            this.workbook = workbook;
            this.sheet = sheet;
        }

        public void open() {
            writeEditor.begin(workbook, sheet);
            closed = false;
        }

        @Override
        public void writeRecord(final String[] line) {
            final HSSFRow row = writeEditor.createRow(rowNum);
            rowNum++;

            for (int i = 0; i < line.length; i++) {
                final String s = line[i];
                final HSSFCell cell = writeEditor.createCell(row, (short) i);
                writeEditor.setCellValue(cell, s);
            }
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            sheet = null;
        }

        public void setWriteEditor(final WriteEditor writeEditor) {
            this.writeEditor = writeEditor;
        }

    }

    public interface WriteEditor {

        HSSFRow createRow(int rowNum);

        void begin(HSSFWorkbook workbook, HSSFSheet sheet);

        void setCellValue(HSSFCell cell, String value);

        HSSFCell createCell(HSSFRow row, short i);

    }

    public static class DefaultWriteEditor implements WriteEditor {

        private static final WriteEditor INSTANCE = new DefaultWriteEditor();

        private HSSFWorkbook workbook;
        private HSSFSheet sheet;

        static WriteEditor getInstance() {
            return INSTANCE;
        }

        @Override
        public void begin(final HSSFWorkbook workbook, final HSSFSheet sheet) {
            this.workbook = workbook;
            this.sheet = sheet;
        }

        @Override
        public HSSFRow createRow(final int rowNum) {
            return sheet.createRow(rowNum);
        }

        @Override
        public HSSFCell createCell(final HSSFRow row, final short colNum) {
            return row.createCell(colNum);
        }

        @Override
        public void setCellValue(final HSSFCell cell, final String value) {
            cell.setCellValue(new HSSFRichTextString(value));
        }

        protected HSSFWorkbook getWorkbook() {
            return workbook;
        }

        protected HSSFSheet getSheet() {
            return sheet;
        }

    }

}
