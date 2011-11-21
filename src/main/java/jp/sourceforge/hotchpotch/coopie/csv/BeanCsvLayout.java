package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.util.FailureProtection;
import jp.sourceforge.hotchpotch.coopie.util.IOUtil;

public class BeanCsvLayout<T> extends AbstractBeanCsvLayout<T> implements
        CsvLayout<T> {

    private final CsvSetting csvSetting_ = new CsvSetting();

    public BeanCsvLayout(final Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public RecordReader<T> openReader(final Reader reader) {
        if (reader == null) {
            throw new NullPointerException("reader");
        }

        final DefaultRecordReader<T> r = new DefaultRecordReader<T>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementSetting(csvSetting_);
        if (getReadEditor() != null) {
            r.setReadEditor(getReadEditor());
        }

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
    public RecordWriter<T> openWriter(final Writer writer) {
        if (writer == null) {
            throw new NullPointerException("writer");
        }

        final DefaultRecordWriter<T> w = new DefaultRecordWriter<T>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        w.setElementSetting(csvSetting_);
        // TODO openで例外時にcloseすること
        w.open(writer);
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
