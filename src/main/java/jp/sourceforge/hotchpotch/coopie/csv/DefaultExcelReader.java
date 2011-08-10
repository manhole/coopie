package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.IOUtil;
import jp.sourceforge.hotchpotch.coopie.LoggerFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

class DefaultExcelReader<T> extends AbstractCsvReader<T> {

    public DefaultExcelReader(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void open(final InputStream is) {
        final PoiReader poiReader = new PoiReader(is);
        poiReader.focusSheet(0);
        elementReader = poiReader;
        closed = false;

        setupByHeader();
    }

    public void openSheetReader(final HSSFSheet sheet) {
        final PoiSheetReader poiReader = new PoiSheetReader(null, sheet);
        elementReader = poiReader;
        closed = false;

        setupByHeader();
    }

    static class PoiReader implements CsvElementReader {

        private HSSFWorkbook workbook;
        private PoiSheetReader sheetReader;
        private boolean closed = true;
        private final List<PoiSheetReader> sheets = CollectionsUtil
                .newArrayList();

        public PoiReader(final InputStream is) {
            try {
                workbook = new HSSFWorkbook(is);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            } finally {
                IOUtil.closeNoException(is);
            }
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

        public int getSheetSize() {
            return workbook.getNumberOfSheets();
        }

        void focusSheet(final int sheetNo) {
            sheetReader = getSheet(sheetNo);
        }

        public PoiSheetReader getSheet(final int sheetNo) {
            if (sheetNo < sheets.size()) {
                final PoiSheetReader r = sheets.get(sheetNo);
                if (r != null) {
                    return r;
                }
            } else {
                final int need = sheetNo + 1 - sheets.size();
                for (int i = 0; i < need; i++) {
                    sheets.add(null);
                }
            }

            final HSSFSheet sheet = workbook.getSheetAt(sheetNo);
            final PoiSheetReader reader = new PoiSheetReader(workbook, sheet);
            sheets.set(sheetNo, reader);

            return reader;
        }

    }

    static class PoiSheetReader implements CsvElementReader {

        private static final Logger logger = LoggerFactory.getLogger();
        protected boolean closed = true;
        private final HSSFWorkbook workbook;
        private HSSFSheet sheet;
        /*
         * Excelの行番号は0オリジン。(見た目は1からだが)
         */
        private int rowNum = 0;
        private final int lastRowNum;

        public PoiSheetReader(final HSSFWorkbook workbook, final HSSFSheet sheet) {
            this.workbook = workbook;
            this.sheet = sheet;
            /*
             * 行番号。
             * 0オリジン。
             * 4行目まである場合は3になる。
             */
            lastRowNum = sheet.getLastRowNum();
            logger.debug("lastRowNum={}", lastRowNum);
            closed = false;
        }

        public String getSheetName() {
            final int sheetIndex = workbook.getSheetIndex(sheet);
            return workbook.getSheetName(sheetIndex);
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
                 * 列番号。0オリジン。
                 * C列まである場合は3が返る
                 */
                final short lastCellNum = row.getLastCellNum();
                if (lastCellNum < 0) {
                    /*
                     * rowがあるのにlastCellNumが-1を返すことがある。
                     * (どうやって作るのかはわからないが)
                     * ここでは空行と同じ扱いにする。
                     */
                    logger.warn("lastCellNum={}, rowNum={}", lastCellNum,
                            rowNum);
                    line = new String[0];
                } else {
                    //logger.debug("lastCellNum={}", lastCellNum);
                    line = new String[lastCellNum];
                    for (short colNo = 0; colNo < lastCellNum; colNo++) {
                        final HSSFCell cell = row.getCell(colNo);
                        final String v = getValueAsString(cell);
                        line[colNo] = v;
                    }
                }
            } else {
                // 空行だとrowがnullになる。
                line = new String[0];
            }

            if (logger.isDebugEnabled()) {
                logger.debug("row({}): {}", rowNum, Arrays.asList(line));
            }

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

        /**
         * @return このsheetが空の場合はtrue
         */
        public boolean isEmpty() {
            if (lastRowNum != 0) {
                return false;
            }

            // シートが空の場合はrowがnull
            final HSSFRow row = sheet.getRow(0);
            if (row != null) {
                return false;
            }

            return true;
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
            return (int) numericValue == numericValue;
        }

    }

}
