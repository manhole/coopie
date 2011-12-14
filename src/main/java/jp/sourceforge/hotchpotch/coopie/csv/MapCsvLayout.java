package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.FailureProtection;

public class MapCsvLayout extends AbstractMapCsvLayout implements
        RecordInOut<Map<String, String>> {

    private final CsvSetting csvSetting_;
    private final ElementInOut elementInOut_;

    public MapCsvLayout() {
        csvSetting_ = new DefaultCsvSetting();
        elementInOut_ = new CsvElementInOut(csvSetting_);
    }

    @Override
    public RecordReader<Map<String, String>> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        final DefaultRecordReader<Map<String, String>> r = new DefaultRecordReader<Map<String, String>>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementInOut(elementInOut_);
        r.setReadEditor(getReadEditor());
        r.setElementEditor(getElementEditor());
        new FailureProtection<RuntimeException>() {

            @Override
            protected void protect() {
                r.open(readable);
            }

            @Override
            protected void rescue() {
                CloseableUtil.closeNoException(r);
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
        w.setElementInOut(elementInOut_);
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
