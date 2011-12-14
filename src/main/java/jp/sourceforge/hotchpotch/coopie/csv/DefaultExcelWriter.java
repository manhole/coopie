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

    private WriteEditor writeEditor_ = DefaultWriteEditor.getInstance();

    public DefaultExcelWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void open(final OutputStream os) {
        final PoiWriter w = new PoiWriter(os);
        w.setWriteEditor(writeEditor_);
        w.open();
        setElementWriter(w);
        setClosed(false);
    }

    public void openSheetWriter(final HSSFWorkbook workbook,
            final HSSFSheet sheet) {
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

        private HSSFWorkbook workbook_;
        private PoiSheetWriter sheetWriter_;
        private WriteEditor writeEditor_ = DefaultWriteEditor.getInstance();

        public PoiWriter(final OutputStream os) {
            os_ = os;
        }

        public void open() {
            workbook_ = new HSSFWorkbook();
            final HSSFSheet sheet = workbook_.createSheet();
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
            IOUtil.closeNoException(sheetWriter_);
            if (workbook_ != null) {
                workbook_.write(os_);
                workbook_ = null;
                IOUtil.closeNoException(os_);
            }
        }

        public void setWriteEditor(final WriteEditor writeEditor) {
            writeEditor_ = writeEditor;
        }

    }

    static class PoiSheetWriter implements ElementWriter {

        private boolean closed_ = true;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);

        private final HSSFWorkbook workbook_;
        private HSSFSheet sheet_;
        private int rowNum_;
        private WriteEditor writeEditor_ = DefaultWriteEditor.getInstance();

        public PoiSheetWriter(final HSSFWorkbook workbook, final HSSFSheet sheet) {
            workbook_ = workbook;
            sheet_ = sheet;
        }

        public void open() {
            writeEditor_.begin(workbook_, sheet_);
            closed_ = false;
        }

        @Override
        public void writeRecord(final String[] line) {
            final HSSFRow row = writeEditor_.createRow(rowNum_);
            rowNum_++;

            for (int i = 0; i < line.length; i++) {
                final String s = line[i];
                final HSSFCell cell = writeEditor_.createCell(row, (short) i);
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

        HSSFRow createRow(int rowNum);

        void begin(HSSFWorkbook workbook, HSSFSheet sheet);

        void setCellValue(HSSFCell cell, String value);

        HSSFCell createCell(HSSFRow row, short i);

    }

    public static class DefaultWriteEditor implements WriteEditor {

        private static final WriteEditor INSTANCE = new DefaultWriteEditor();

        private HSSFWorkbook workbook_;
        private HSSFSheet sheet_;

        static WriteEditor getInstance() {
            return INSTANCE;
        }

        @Override
        public void begin(final HSSFWorkbook workbook, final HSSFSheet sheet) {
            workbook_ = workbook;
            sheet_ = sheet;
        }

        @Override
        public HSSFRow createRow(final int rowNum) {
            return sheet_.createRow(rowNum);
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
            return workbook_;
        }

        protected HSSFSheet getSheet() {
            return sheet_;
        }

    }

}
