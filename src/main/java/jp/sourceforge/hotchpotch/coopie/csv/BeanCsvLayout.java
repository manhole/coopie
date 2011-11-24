package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.FailureProtection;
import jp.sourceforge.hotchpotch.coopie.util.IOUtil;

public class BeanCsvLayout<T> extends AbstractBeanCsvLayout<T> implements
        CsvLayout<T> {

    private final CsvSetting csvSetting_;
    private final ElementSetting elementSetting_;

    public static <T> BeanCsvLayout<T> getInstance(final Class<T> beanClass) {
        final BeanCsvLayout<T> instance = new BeanCsvLayout<T>(beanClass);
        return instance;
    }

    public BeanCsvLayout(final Class<T> beanClass) {
        super(beanClass);
        csvSetting_ = new DefaultCsvSetting();
        elementSetting_ = new CsvElementSetting(csvSetting_);
    }

    @Override
    public RecordReader<T> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        final DefaultRecordReader<T> r = new DefaultRecordReader<T>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementSetting(elementSetting_);
        r.setReadEditor(getReadEditor());

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
    public RecordWriter<T> openWriter(final Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        final DefaultRecordWriter<T> w = new DefaultRecordWriter<T>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        w.setElementSetting(elementSetting_);
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
