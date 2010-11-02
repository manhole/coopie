package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

public class BeanCsvLayout<T> extends AbstractBeanCsvLayout<T> implements
        CsvLayout<T> {

    public BeanCsvLayout(final Class<T> beanClass) {
        super(beanClass);
    }

    public CsvReader<T> openReader(final Reader reader) {
        final DefaultCsvReader<T> r = new DefaultCsvReader<T>(buildRecordDesc());
        // TODO openで例外時にcloseすること
        r.open(reader);
        return r;
    }

    public CsvWriter<T> openWriter(final Writer writer) {
        final DefaultCsvWriter<T> w = new DefaultCsvWriter<T>(buildRecordDesc());
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

}
