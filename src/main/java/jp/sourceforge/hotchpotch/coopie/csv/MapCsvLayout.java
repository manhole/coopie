package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.FailureProtection;
import jp.sourceforge.hotchpotch.coopie.IOUtil;

public class MapCsvLayout extends AbstractMapCsvLayout implements
        CsvLayout<Map<String, String>> {

    private final CsvSetting csvSetting = new CsvSetting();

    @Override
    public RecordReader<Map<String, String>> openReader(final Reader reader) {
        if (reader == null) {
            throw new NullPointerException("reader");
        }

        final DefaultRecordReader<Map<String, String>> r = new DefaultRecordReader<Map<String, String>>(
                getRecordDesc());
        r.setWithHeader(withHeader);
        r.setElementSetting(csvSetting);
        new FailureProtection<RuntimeException>() {

            @Override
            protected void protect() {
                r.open(reader);
            }

            @Override
            protected void rescue() {
                IOUtil.closeNoException(r);
            }

        }.execute();
        return r;
    }

    @Override
    public RecordWriter<Map<String, String>> openWriter(final Writer writer) {
        if (writer == null) {
            throw new NullPointerException("writer");
        }

        final DefaultRecordWriter<Map<String, String>> w = new DefaultRecordWriter<Map<String, String>>(
                getRecordDesc());
        w.setWithHeader(withHeader);
        w.setElementSetting(csvSetting);
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

    public void setElementSeparator(final char elementSeparator) {
        csvSetting.setElementSeparator(elementSeparator);
    }

}
