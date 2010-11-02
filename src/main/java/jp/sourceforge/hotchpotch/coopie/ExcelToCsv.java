package jp.sourceforge.hotchpotch.coopie;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import jp.sourceforge.hotchpotch.coopie.csv.CsvElementWriter;
import jp.sourceforge.hotchpotch.coopie.csv.CsvSetting;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

        final HSSFWorkbook workbook = new HSSFWorkbook(
                files.openBufferedInputStream(file));
        final HSSFSheet sheet = workbook.getSheetAt(0);

        final CsvElementWriter csvWriter = new CsvSetting().openWriter(files
                .openBufferedWriter(tsvFile));

        final int lastRowNum = sheet.getLastRowNum();
        for (int rowNo = 0; rowNo <= lastRowNum; rowNo++) {
            final HSSFRow row = sheet.getRow(rowNo);
            final short lastCellNum = row.getLastCellNum();
            final String[] line = new String[lastCellNum];
            for (short colNo = 0; colNo < lastCellNum; colNo++) {
                final HSSFCell cell = row.getCell(colNo);
                final String v = getValueAsString(cell);
                line[colNo] = v;
            }
            logger.debug("row: " + Arrays.asList(line));
            csvWriter.writeRecord(line);
        }

        csvWriter.close();
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
