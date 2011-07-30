package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public class MapCsvLayout extends AbstractMapCsvLayout implements
        CsvLayout<Map<String, String>> {

    @Override
    public CsvReader<Map<String, String>> openReader(final Reader reader) {
        final DefaultCsvReader<Map<String, String>> r = new DefaultCsvReader<Map<String, String>>(
                buildRecordDesc());
        r.setWithHeader(withHeader);
        // TODO openで例外時にcloseすること
        r.open(reader);
        return r;
    }

    @Override
    public CsvWriter<Map<String, String>> openWriter(final Writer writer) {
        final DefaultCsvWriter<Map<String, String>> w = new DefaultCsvWriter<Map<String, String>>(
                buildRecordDesc());
        w.setWithHeader(withHeader);
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

}
