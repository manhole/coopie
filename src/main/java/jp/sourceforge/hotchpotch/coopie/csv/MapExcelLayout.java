package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class MapExcelLayout extends AbstractMapCsvLayout implements
        ExcelLayout<Map<String, String>> {

    @Override
    public CsvReader<Map<String, String>> openReader(final InputStream is) {
        final DefaultExcelReader<Map<String, String>> r = new DefaultExcelReader<Map<String, String>>(
                buildRecordDesc());
        // TODO openで例外時にcloseすること
        r.open(is);
        return r;
    }

    @Override
    public CsvWriter<Map<String, String>> openWriter(final OutputStream os) {
        // TODO Auto-generated method stub
        return null;
    }

}
