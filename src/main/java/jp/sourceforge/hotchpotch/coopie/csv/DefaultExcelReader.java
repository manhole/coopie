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
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

class DefaultExcelReader<BEAN> extends AbstractRecordReader<BEAN> {

    public DefaultExcelReader(final RecordDesc<BEAN> recordDesc) {
        super(recordDesc);
    }

    public void open(final InputStream is) {
        final PoiReader poiReader = new PoiReader(is);
        poiReader.focusSheet(0);
        setElementReader(poiReader);
        setClosed(false);

        setupByHeader();
    }

    public void openSheetReader(final HSSFSheet sheet) {
        final PoiSheetReader poiReader = new PoiSheetReader(null, sheet);
        setElementReader(poiReader);
        setClosed(false);

        setupByHeader();
    }

    static class PoiReader implements ElementReader {

        private HSSFWorkbook workbook_;
        private PoiSheetReader sheetReader_;
        private boolean closed_ = true;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);

        private final List<PoiSheetReader> sheets_ = CollectionsUtil
                .newArrayList();

        public PoiReader(final InputStream is) {
            try {
                workbook_ = new HSSFWorkbook(is);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            } finally {
                CloseableUtil.closeNoException(is);
            }
        }

        @Override
        public int getRecordNumber() {
            return sheetReader_.getRecordNumber();
        }

        @Override
        public String[] readRecord() {
            return sheetReader_.readRecord();
        }

        @Override
        public int getLineNumber() {
            return sheetReader_.getLineNumber();
        }

        @Override
        public boolean isClosed() {
            return closed_;
        }

        @Override
        public void close() throws IOException {
            closed_ = true;
            CloseableUtil.closeNoException(sheetReader_);
            workbook_ = null;
        }

        public int getSheetSize() {
            return workbook_.getNumberOfSheets();
        }

        void focusSheet(final int sheetNo) {
            sheetReader_ = getSheet(sheetNo);
        }

        public PoiSheetReader getSheet(final int sheetNo) {
            if (sheetNo < sheets_.size()) {
                final PoiSheetReader r = sheets_.get(sheetNo);
                if (r != null) {
                    return r;
                }
            } else {
                final int need = sheetNo + 1 - sheets_.size();
                for (int i = 0; i < need; i++) {
                    sheets_.add(null);
                }
            }

            final HSSFSheet sheet = workbook_.getSheetAt(sheetNo);
            final PoiSheetReader reader = new PoiSheetReader(workbook_, sheet);
            sheets_.set(sheetNo, reader);

            return reader;
        }

    }

    static class PoiSheetReader implements ElementReader {

        private static final Logger logger = LoggerFactory.getLogger();
        private boolean closed_ = true;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);

        private final HSSFWorkbook workbook_;
        private HSSFSheet sheet_;
        /*
         * Excelの行番号は0オリジン。(見た目は1からだが)
         */
        private int rowNum_ = 0;
        private final int lastRowNum_;

        public PoiSheetReader(final HSSFWorkbook workbook, final HSSFSheet sheet) {
            workbook_ = workbook;
            sheet_ = sheet;
            /*
             * 行番号。
             * 0オリジン。
             * 4行目まである場合は3になる。
             */
            lastRowNum_ = sheet.getLastRowNum();
            logger.debug("lastRowNum={}", lastRowNum_);
            closed_ = false;
        }

        public String getSheetName() {
            final int sheetIndex = workbook_.getSheetIndex(sheet_);
            return workbook_.getSheetName(sheetIndex);
        }

        @Override
        public int getRecordNumber() {
            return rowNum_;
        }

        @Override
        public int getLineNumber() {
            return rowNum_;
        }

        @Override
        public String[] readRecord() {
            if (lastRowNum_ < rowNum_) {
                return null;
            }
            final HSSFRow row = sheet_.getRow(rowNum_);
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
                            rowNum_);
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
                logger.debug("row({}): {}", rowNum_, Arrays.asList(line));
            }

            rowNum_++;
            return line;
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

        /**
         * @return このsheetが空の場合はtrue
         */
        public boolean isEmpty() {
            if (lastRowNum_ != 0) {
                return false;
            }

            // シートが空の場合はrowがnull
            final HSSFRow row = sheet_.getRow(0);
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
