package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.util.FailureProtection;
import jp.sourceforge.hotchpotch.coopie.util.IOUtil;

public class MapCsvLayout extends AbstractMapCsvLayout implements
        CsvLayout<Map<String, String>> {

    private final CsvSetting csvSetting_ = new CsvSetting();

    @Override
    public RecordReader<Map<String, String>> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        final DefaultRecordReader<Map<String, String>> r = new DefaultRecordReader<Map<String, String>>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementSetting(csvSetting_);
        new FailureProtection<RuntimeException>() {

            @Override
            protected void protect() {
                r.open(readable);
            }

            @Override
            protected void rescue() {
                IOUtil.closeNoException(r);
            }

        }.execute();
        return r;
    }

    @Override
    public RecordWriter<Map<String, String>> openWriter(
            final Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        final DefaultRecordWriter<Map<String, String>> w = new DefaultRecordWriter<Map<String, String>>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        w.setElementSetting(csvSetting_);
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    public void setElementSeparator(final char elementSeparator) {
        csvSetting_.setElementSeparator(elementSeparator);
    }

    public void setLineSeparator(final String lineSeparator) {
        csvSetting_.setLineSeparator(lineSeparator);
    }

    public void setQuoteMark(final char quoteMark) {
        csvSetting_.setQuoteMark(quoteMark);
    }

}
