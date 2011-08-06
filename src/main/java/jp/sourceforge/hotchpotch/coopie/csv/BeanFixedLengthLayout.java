package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

public class BeanFixedLengthLayout<T> extends AbstractFixedLengthLayout<T>
        implements CsvLayout<T> {

    public BeanFixedLengthLayout(final Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public CsvReader<T> openReader(final Reader reader) {
        final FixedLengthRecordDesc<T> rd = buildRecordDesc();
        final DefaultCsvReader<T> r = new DefaultCsvReader<T>(rd);
        r.setWithHeader(withHeader);
        r.setElementSetting(rd);
        // TODO openで例外時にcloseすること
        r.open(reader);
        return r;
    }

    @Override
    public CsvWriter<T> openWriter(final Writer writer) {
        final FixedLengthRecordDesc<T> rd = buildRecordDesc();
        final DefaultCsvWriter<T> w = new DefaultCsvWriter<T>(rd);
        w.setWithHeader(withHeader);
        w.setElementSetting(rd);
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

}
