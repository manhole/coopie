package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractBeanCsvLayout;
import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.SimpleColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.Annotations;
import jp.sourceforge.hotchpotch.coopie.csv.BeanRecordType;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordInOut;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.RecordType;
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;

public class BeanFixedLengthLayout<T> extends AbstractFixedLengthLayout<T>
        implements RecordInOut<T> {

    private final BeanDesc<T> beanDesc_;

    public static <T> BeanFixedLengthLayout<T> getInstance(
            final Class<T> beanClass) {
        final BeanFixedLengthLayout<T> instance = new BeanFixedLengthLayout<T>(
                beanClass);
        return instance;
    }

    public BeanFixedLengthLayout(final Class<T> beanClass) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass);
    }

    @Override
    public RecordReader<T> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        final RecordDesc<T> rd = myRecordDesc();
        final DefaultRecordReader<T> r = new DefaultRecordReader<T>(rd);
        r.setWithHeader(isWithHeader());
        r.setElementInOut(createElementInOut());
        r.setElementEditor(getElementEditor());
        // TODO openで例外時にcloseすること
        r.open(readable);
        return r;
    }

    @Override
    public RecordWriter<T> openWriter(final Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        final RecordDesc<T> rd = myRecordDesc();
        final DefaultRecordWriter<T> w = new DefaultRecordWriter<T>(rd);
        w.setWithHeader(isWithHeader());
        w.setElementInOut(createElementInOut());
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    @Override
    protected FixedLengthRecordDescSetup getRecordDescSetup() {
        return new BeanFixedLengthRecordDescSetup<T>(beanDesc_);
    }

    protected RecordDesc<T> myRecordDesc() {
        if (getRecordDesc() == null) {
            /*
             * アノテーションが付いている場合は、アノテーションから構築する
             */
            setupByAnnotation();
        }

        final RecordDesc<T> recordDesc = getRecordDesc();
        if (recordDesc == null) {
            throw new IllegalStateException("recordDesc");
        }
        return recordDesc;
    }

    private void setupByAnnotation() {
        final List<PdAndColumn<T>> cols = CollectionsUtil.newArrayList();
        final List<PropertyDesc<T>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<T> pd : pds) {
            final FixedLengthColumn column = Annotations.getAnnotation(pd,
                    FixedLengthColumn.class);
            if (column == null) {
                continue;
            }
            cols.add(new PdAndColumn<T>(pd, column));
        }

        if (cols.isEmpty()) {
            return;
        }

        setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                for (final PdAndColumn<T> col : cols) {
                    setup.column(col.getPropertyName(), col.getBeginIndex(),
                            col.getEndIndex());
                }
            }
        });
    }

    private static class PdAndColumn<T> {

        private final PropertyDesc<T> desc_;
        private final FixedLengthColumn column_;

        public PdAndColumn(final PropertyDesc<T> desc,
                final FixedLengthColumn column) {
            desc_ = desc;
            column_ = column;
        }

        public String getPropertyName() {
            return desc_.getPropertyName();
        }

        public int getBeginIndex() {
            return column_.beginIndex();
        }

        public int getEndIndex() {
            return column_.endIndex();
        }

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
        protected ColumnDesc<T>[] createColumnDescs(
                final List<SimpleColumnBuilder> builders) {
            final ColumnDesc<T>[] cds = AbstractBeanCsvLayout.toColumnDescs(
                    builders, beanDesc_);
            return cds;
        }

    }

}
