package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.File;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.FileOperation;
import jp.sourceforge.hotchpotch.coopie.FileResource;
import jp.sourceforge.hotchpotch.coopie.LoggerFactory;

import org.slf4j.Logger;

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

        final FileResource fr = files.getFileResource(file);
        final File tsvFile = files.createFile(file.getParentFile(),
                fr.getPrefix() + TSV_EXTENSION);

        final DefaultExcelReader.PoiReader poiReader = new DefaultExcelReader.PoiReader(
                files.openBufferedInputStream(file));

        final CsvElementWriter csvWriter = new CsvSetting().openWriter(files
                .openBufferedWriter(tsvFile));

        while (true) {
            final String[] line = poiReader.readRecord();
            if (line == null) {
                break;
            }
            csvWriter.writeRecord(line);
        }

        csvWriter.close();
    }

}
