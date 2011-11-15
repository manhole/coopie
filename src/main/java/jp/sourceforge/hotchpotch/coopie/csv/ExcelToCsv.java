package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.FileOperation;
import jp.sourceforge.hotchpotch.coopie.FileResource;
import jp.sourceforge.hotchpotch.coopie.IOUtil;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultExcelReader.PoiSheetReader;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

public class ExcelToCsv {

    private static final Logger logger = LoggerFactory.getLogger();
    private final FileOperation files = new FileOperation();
    private static final String TSV_EXTENSION = ".tsv";

    public void writeTsv(final File file) throws IOException {
        logger.debug("file={}", file.getAbsolutePath());
        if (!file.exists()) {
            throw new IllegalArgumentException("not exist:"
                    + file.getAbsolutePath());
        }

        final DefaultExcelReader.PoiReader poiReader = new DefaultExcelReader.PoiReader(
                files.openBufferedInputStream(file));

        final List<PoiSheetReader> sheets = CollectionsUtil.newArrayList();
        try {
            final int sheetSize = poiReader.getSheetSize();
            for (int sheetNo = 0; sheetNo < sheetSize; sheetNo++) {
                final PoiSheetReader sheet = poiReader.getSheet(sheetNo);
                if (!sheet.isEmpty()) {
                    sheets.add(sheet);
                } else {
                    IOUtil.closeNoException(sheet);
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

            final CsvSetting csvSetting = new CsvSetting();
            final FileResource fr = files.getFileResource(file);
            for (final PoiSheetReader sheetReader : sheets) {
                final String fileName = tsvNaming.createFileName(sheetReader,
                        fr.getPrefix(), TSV_EXTENSION);
                final File tsvFile = files.createFile(file.getParentFile(),
                        fileName);

                final CsvElementWriter csvWriter = csvSetting.openWriter(files
                        .openBufferedWriter(tsvFile));

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
                IOUtil.closeNoException(sheetReader);
            }
        }

        poiReader.close();
    }

    private static class TsvNaming {

        private boolean single = true;

        public String createFileName(final PoiSheetReader sheetReader,
                final String prefix, final String suffix) {
            if (single) {
                return prefix + suffix;
            } else {
                return prefix + "-" + sheetReader.getSheetName() + suffix;
            }
        }

        public void setSingle(final boolean single) {
            this.single = single;
        }

    }

}
