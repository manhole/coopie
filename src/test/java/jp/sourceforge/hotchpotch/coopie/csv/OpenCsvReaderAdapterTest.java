package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStreamReader;
import java.nio.charset.Charset;

import au.com.bytecode.opencsv.CSVReader;

public class OpenCsvReaderAdapterTest extends CsvElementReaderTest {

    @Override
    protected ElementReader constructTest1Reader() {
        final InputStreamReader reader = new InputStreamReader(
                BeanCsvReaderTest.getResourceAsStream("-1", "tsv"),
                Charset.forName("UTF-8"));
        final CSVReader csvReader = new CSVReader(reader, CsvSetting.TAB,
                CsvSetting.DOUBLE_QUOTE);
        return new OpenCsvReaderAdapter(csvReader);
    }

}
