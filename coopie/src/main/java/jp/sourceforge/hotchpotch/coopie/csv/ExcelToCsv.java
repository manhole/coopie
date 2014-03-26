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

import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.DefaultExcelReader.PoiSheetReader;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.FileOperation;
import jp.sourceforge.hotchpotch.coopie.util.FileResource;

import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

public class ExcelToCsv {

    private static final Logger logger = LoggerFactory.getLogger();
    private final FileOperation files_ = new FileOperation();
    private static final String TSV_EXTENSION = ".tsv";

    public void writeTsv(final File file) throws IOException {
        logger.debug("file={}", file.getAbsolutePath());
        if (!file.exists()) {
            throw new IllegalArgumentException("not exist:" + file.getAbsolutePath());
        }

        final DefaultExcelReader.PoiReader poiReader = new DefaultExcelReader.PoiReader(
                files_.openBufferedInputStream(file));

        final List<PoiSheetReader> sheets = CollectionsUtil.newArrayList();
        try {
            final int sheetSize = poiReader.getSheetSize();
            for (int sheetNo = 0; sheetNo < sheetSize; sheetNo++) {
                final PoiSheetReader sheet = poiReader.getSheet(sheetNo);
                if (!sheet.isEmpty()) {
                    sheets.add(sheet);
                } else {
                    CloseableUtil.closeNoException(sheet);
                }
            }

            final TsvNaming tsvNaming = new TsvNaming();

            if (0 == sheets.size()) {
                // TODO
                // excelが全て空の場合は、1シート目を代表にしておく
                //tsvNaming.setSingle(true);
                //sheets.add(poiReader.getSheet(0));
            } else if (1 == sheets.size()) {
                tsvNaming.setSingle(true);
            } else if (1 < sheets.size()) {
                tsvNaming.setSingle(false);
            }

            final CsvSetting csvSetting = new DefaultCsvSetting();
            final ElementInOut elementInOut = new CsvElementInOut(csvSetting);
            final FileResource fr = files_.getFileResource(file);
            for (final PoiSheetReader sheetReader : sheets) {
                final String fileName = tsvNaming.createFileName(sheetReader, fr.getPrefix(), TSV_EXTENSION);
                final File tsvFile = files_.createFile(file.getParentFile(), fileName);

                final ElementWriter csvWriter = elementInOut.openWriter(files_.openBufferedWriter(tsvFile));

                while (true) {
                    final String[] line = sheetReader.readRecord();
                    if (line == null) {
                        break;
                    }
                    csvWriter.writeRecord(line);
                }

                csvWriter.close();
            }
        } finally {
            for (final PoiSheetReader sheetReader : sheets) {
                CloseableUtil.closeNoException(sheetReader);
            }
        }

        poiReader.close();
    }

    private static class TsvNaming {

        private boolean single_ = true;

        public String createFileName(final PoiSheetReader sheetReader, final String prefix, final String suffix) {
            if (single_) {
                return prefix + suffix;
            } else {
                return prefix + "-" + sheetReader.getSheetName() + suffix;
            }
        }

        public void setSingle(final boolean single) {
            single_ = single;
        }

    }

}
