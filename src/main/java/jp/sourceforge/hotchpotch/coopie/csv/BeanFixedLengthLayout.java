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
        final RecordDesc<T> rd = getRecordDesc();
        final ElementSetting es = getElementSetting();
        final DefaultCsvReader<T> r = new DefaultCsvReader<T>(rd);
        r.setWithHeader(withHeader);
        r.setElementSetting(es);
        // TODO openで例外時にcloseすること
        r.open(reader);
        return r;
    }

    @Override
    public CsvWriter<T> openWriter(final Writer writer) {
        final RecordDesc<T> rd = getRecordDesc();
        final ElementSetting es = getElementSetting();
        final DefaultCsvWriter<T> w = new DefaultCsvWriter<T>(rd);
        w.setWithHeader(withHeader);
        w.setElementSetting(es);
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

    @Override
    protected FixedLengthColumnSetup<T> getRecordDescSetup() {
        return new DefaultFixedLengthColumnSetup<T>(beanDesc);
    }

}
