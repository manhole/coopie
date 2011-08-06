package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;

public class BeanFixedLengthLayout<T> extends AbstractFixedLengthLayout<T>
        implements CsvLayout<T> {

    private final BeanDesc<T> beanDesc;

    public BeanFixedLengthLayout(final Class<T> beanClass) {
        beanDesc = BeanDescFactory.getBeanDesc(beanClass);
    }

    @Override
    public CsvReader<T> openReader(final Reader reader) {
        final FixedLengthRecordDesc<T> rd = getRecordDesc();
        final DefaultCsvReader<T> r = new DefaultCsvReader<T>(rd);
        r.setWithHeader(withHeader);
        r.setElementSetting(rd);
        // TODO openで例外時にcloseすること
        r.open(reader);
        return r;
    }

    @Override
    public CsvWriter<T> openWriter(final Writer writer) {
        final FixedLengthRecordDesc<T> rd = getRecordDesc();
        final DefaultCsvWriter<T> w = new DefaultCsvWriter<T>(rd);
        w.setWithHeader(withHeader);
        w.setElementSetting(rd);
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

    @Override
    protected DefaultFixedLengthColumnSetup<T> getRecordDescSetup() {
        return new DefaultFixedLengthColumnSetup<T>(beanDesc);
    }

}
