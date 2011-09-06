package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.FailureProtection;
import jp.sourceforge.hotchpotch.coopie.IOUtil;

public class BeanCsvLayout<T> extends AbstractBeanCsvLayout<T> implements
        CsvLayout<T> {

    private final CsvSetting csvSetting = new CsvSetting();

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
        r.setWithHeader(withHeader);
        r.setElementSetting(csvSetting);
        if (readEditor != null) {
            r.setReadEditor(readEditor);
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
