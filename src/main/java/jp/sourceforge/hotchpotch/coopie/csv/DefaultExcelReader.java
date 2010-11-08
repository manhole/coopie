package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import jp.sourceforge.hotchpotch.coopie.IOUtil;
import jp.sourceforge.hotchpotch.coopie.LoggerFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.t2framework.commons.exception.IORuntimeException;

class DefaultExcelReader<T> extends AbstractCsvReader<T> {

    public DefaultExcelReader(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void open(final InputStream is) {
        elementReader = new PoiReader(is);
        closed = false;

        setupByHeader();
    }

    static class PoiReader implements CsvElementReader {

        private HSSFWorkbook workbook;
        private final PoiSheetReader sheetReader;
        private boolean closed = true;

        public PoiReader(final InputStream is) {
            try {
                workbook = new HSSFWorkbook(is);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            } finally {
                IOUtil.closeNoException(is);
            }
            final HSSFSheet sheet = workbook.getSheetAt(0);
            sheetReader = new PoiSheetReader(sheet);
        }

        @Override
        public String[] readRecord() {
            return sheetReader.readRecord();
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            IOUtil.closeNoException(sheetReader);
            workbook = null;
        }

    }

    static class PoiSheetReader implements CsvElementReader {

        private static final Logger logger = LoggerFactory.getLogger();
        protected boolean closed = true;
        private HSSFSheet sheet;
        private int rowNum = 0;
        private final int lastRowNum;

        public PoiSheetReader(final HSSFSheet sheet) {
            this.sheet = sheet;
            /*
             * 0オリジン。
             * 4行目まである場合は3になる。
             */
            lastRowNum = sheet.getLastRowNum();
            logger.debug("lastRowNum={}", lastRowNum);
            closed = false;
        }

        @Override
        public String[] readRecord() {
            if (lastRowNum < rowNum) {
                return null;
            }
            final HSSFRow row = sheet.getRow(rowNum);
            final String[] line;
            if (row != null) {
                /*
                 * C列まである場合は3が返る
                 */
                final short lastCellNum = row.getLastCellNum();
                //logger.debug("lastCellNum={}", lastCellNum);
                line = new String[lastCellNum];
                for (short colNo = 0; colNo < lastCellNum; colNo++) {
                    final HSSFCell cell = row.getCell(colNo);
                    final String v = getValueAsString(cell);
                    line[colNo] = v;
                }
            } else {
                // 空行だとrowがnullになる。
                line = new String[0];
            }
            logger.debug("row({}): {}", rowNum, Arrays.asList(line));

            rowNum++;
            return line;
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

        private String getValueAsString(final HSSFCell cell) {
            if (cell == null) {
                return null;
            }
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                final double v = cell.getNumericCellValue();
                if (isInt(v)) {
                    return Integer.toString((int) v);
                }
                return Double.toString(v);
            case HSSFCell.CELL_TYPE_BOOLEAN:
                final boolean b = cell.getBooleanCellValue();
                return Boolean.toString(b);
            case HSSFCell.CELL_TYPE_STRING:
            default:
                final HSSFRichTextString richStringCellValue = cell
                        .getRichStringCellValue();
                final String value = richStringCellValue.getString();
                return value;
            }
        }

        private boolean isInt(final double numericValue) {
            return ((int) numericValue) == numericValue;
        }

    }

}
