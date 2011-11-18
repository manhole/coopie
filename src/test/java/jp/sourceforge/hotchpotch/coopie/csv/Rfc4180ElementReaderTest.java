package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Rfc4180ElementReaderTest extends CsvElementReaderTest {

    @Override
    protected ElementReader constructTest1Reader() {
        final InputStreamReader reader = new InputStreamReader(
                BeanCsvReaderTest.getResourceAsStream("-1", "tsv"),
                Charset.forName("UTF-8"));
        final Rfc4180Reader csvReader = new Rfc4180Reader();
        csvReader.setElementSeparator(CsvSetting.TAB);
        csvReader.setQuoteMark(CsvSetting.DOUBLE_QUOTE);
        csvReader.open(reader);
        return csvReader;
    }

}
