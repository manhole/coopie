package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class PoiSheetReaderTest extends CsvElementReaderTest {

    @Override
    protected CsvElementReader constructTest1Reader() throws Throwable {
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1",
                "xls");
        final HSSFWorkbook workbook = new HSSFWorkbook(is);
        final DefaultExcelReader.PoiSheetReader poiReader = new DefaultExcelReader.PoiSheetReader(
                workbook, workbook.getSheetAt(0));
        return poiReader;
    }

}
