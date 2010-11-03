package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.OutputStream;

import jp.sourceforge.hotchpotch.coopie.IOUtil;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class DefaultExcelWriter<T> extends AbstractCsvWriter<T> {

    public DefaultExcelWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void open(final OutputStream os) {
        elementWriter = new PoiWriter(os);
        closed = false;
    }

    static class PoiWriter implements CsvElementWriter {

        private final OutputStream os;
        private boolean closed = true;
        private HSSFWorkbook workbook;
        private final HSSFSheet sheet;
        private int rowNum;

        public PoiWriter(final OutputStream os) {
            this.os = os;

            workbook = new HSSFWorkbook();
            sheet = workbook.createSheet();

            closed = false;
        }

        @Override
        public void writeRecord(final String[] line) {
            final HSSFRow row = sheet.createRow(rowNum);
            rowNum++;

            for (int i = 0; i < line.length; i++) {
                final String s = line[i];
                final HSSFCell cell = row.createCell((short) i);
                cell.setCellValue(new HSSFRichTextString(s));
            }
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if (workbook != null) {
                workbook.write(os);
                workbook = null;
                IOUtil.closeNoException(os);
            }
        }

    }

}
