package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.FailureProtection;

public class MapCsvLayout<PROP> extends AbstractMapCsvLayout<PROP> implements
        RecordInOut<Map<String, PROP>> {

    private final CsvSetting csvSetting_;

    public MapCsvLayout() {
        csvSetting_ = new DefaultCsvSetting();
    }

    @Override
    public RecordReader<Map<String, PROP>> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        prepareOpen();
        final DefaultRecordReader<Map<String, PROP>> r = new DefaultRecordReader<Map<String, PROP>>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementInOut(createElementInOut());
        r.setElementReaderHandler(getElementReaderHandler());
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
    public RecordWriter<Map<String, PROP>> openWriter(
            final Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        prepareOpen();
        final DefaultRecordWriter<Map<String, PROP>> w = new DefaultRecordWriter<Map<String, PROP>>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        w.setElementInOut(createElementInOut());
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

    public void setQuoteMode(final QuoteMode quoteMode) {
        csvSetting_.setQuoteMode(quoteMode);
    }

    protected ElementInOut createElementInOut() {
        final CsvElementInOut a = new CsvElementInOut(csvSetting_);
        a.setLineReaderHandler(getLineReaderHandler());
        return a;
    }

}
