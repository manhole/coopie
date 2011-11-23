package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;

public class PoiReaderTest extends ElementReaderTest {

    @Override
    protected ElementReader constructTest1Reader() {
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1",
                "xls");
        final DefaultExcelReader.PoiReader poiReader = new DefaultExcelReader.PoiReader(
                is);
        poiReader.focusSheet(0);
        return poiReader;
    }

}
