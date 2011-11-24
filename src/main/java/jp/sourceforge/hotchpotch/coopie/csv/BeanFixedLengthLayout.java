package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;

public class BeanFixedLengthLayout<T> extends AbstractFixedLengthLayout<T>
        implements CsvLayout<T> {

    private final BeanDesc<T> beanDesc_;

    public BeanFixedLengthLayout(final Class<T> beanClass) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass);
    }

    @Override
    public RecordReader<T> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        final RecordDesc<T> rd = getRecordDesc();
        final ElementSetting es = getElementSetting();
        final DefaultRecordReader<T> r = new DefaultRecordReader<T>(rd);
        r.setWithHeader(isWithHeader());
        r.setElementSetting(es);
        // TODO openで例外時にcloseすること
        r.open(readable);
        return r;
    }

    @Override
    public RecordWriter<T> openWriter(final Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        final RecordDesc<T> rd = getRecordDesc();
        final ElementSetting es = getElementSetting();
        final DefaultRecordWriter<T> w = new DefaultRecordWriter<T>(rd);
        w.setWithHeader(isWithHeader());
        w.setElementSetting(es);
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    @Override
    protected FixedLengthRecordDescSetup getRecordDescSetup() {
        return new BeanFixedLengthRecordDescSetup<T>(beanDesc_);
    }

    private static class BeanFixedLengthRecordDescSetup<T> extends
            AbstractFixedLengthRecordDescSetup<T> {

        private final BeanDesc<T> beanDesc_;

        BeanFixedLengthRecordDescSetup(final BeanDesc<T> beanDesc) {
            beanDesc_ = beanDesc;
        }

        @Override
        protected RecordType<T> getRecordType() {
            return new BeanRecordType<T>(beanDesc_);
        }

        @Override
        protected ColumnDesc<T>[] createColumnDesc(
                final List<ColumnName> columnNames) {
            final ColumnDesc<T>[] cds = AbstractBeanCsvLayout.toColumnDescs(
                    columnNames, beanDesc_);
            return cds;
        }

    }

}
