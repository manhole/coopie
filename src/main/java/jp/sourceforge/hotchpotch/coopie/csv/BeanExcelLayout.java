package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;

public class BeanExcelLayout<T> extends AbstractBeanCsvLayout<T> implements
        ExcelLayout<T> {

    public BeanExcelLayout(final Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public CsvReader<T> openReader(final InputStream is) {
        final DefaultExcelReader<T> r = new DefaultExcelReader<T>(
                buildRecordDesc());
        // TODO openで例外時にcloseすること
        r.open(is);
        return r;
    }

    @Override
    public CsvWriter<T> openWriter(final OutputStream os) {
        final DefaultExcelWriter<T> w = new DefaultExcelWriter<T>(
                buildRecordDesc());
        // TODO openで例外時にcloseすること
        w.open(os);
        return w;
    }

}
