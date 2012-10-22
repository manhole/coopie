package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.Annotations;
import jp.sourceforge.hotchpotch.coopie.csv.BeanPropertyBinding;
import jp.sourceforge.hotchpotch.coopie.csv.BeanRecordType;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.PropertyBindingFactory;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordInOut;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public class BeanFixedLengthLayout<BEAN> extends
        AbstractFixedLengthLayout<BEAN> implements RecordInOut<BEAN> {

    private final BeanDesc<BEAN> beanDesc_;

    public static <BEAN> BeanFixedLengthLayout<BEAN> getInstance(
            final Class<BEAN> beanClass) {
        final BeanFixedLengthLayout<BEAN> instance = new BeanFixedLengthLayout<BEAN>(
                beanClass);
        return instance;
    }

    public BeanFixedLengthLayout(final Class<BEAN> beanClass) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass);
    }

    @Override
    public RecordReader<BEAN> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        prepareOpen();
        final RecordDesc<BEAN> rd = getRecordDesc();
        final DefaultRecordReader<BEAN> r = new DefaultRecordReader<BEAN>(rd);
        r.setWithHeader(isWithHeader());
        r.setElementInOut(createElementInOut());
        r.setElementEditor(getElementEditor());
        // TODO openで例外時にcloseすること
        r.open(readable);
        return r;
    }

    @Override
    public RecordWriter<BEAN> openWriter(final Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        prepareOpen();
        final RecordDesc<BEAN> rd = getRecordDesc();
        final DefaultRecordWriter<BEAN> w = new DefaultRecordWriter<BEAN>(rd);
        w.setWithHeader(isWithHeader());
        w.setElementInOut(createElementInOut());
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    protected void prepareOpen() {
        if (getRecordDesc() == null) {
            /*
             * アノテーションが付いている場合は、アノテーションから構築する
             */
            final FixedLengthRecordDef recordDef = recordDef();
            // TODO customizer_.customize(recordDef);
            {
                final FixedLengthElementDesc[] elementDescs = recordDefToElementDescs(recordDef);
                setFixedLengthElementDescs(elementDescs);
            }
            {
                final PropertyBindingFactory<BEAN> pbf = new BeanPropertyBinding.Factory<BEAN>(
                        beanDesc_);
                final BeanRecordType<BEAN> recordType = new BeanRecordType<BEAN>(
                        beanDesc_);
                final RecordDesc<BEAN> recordDesc = createRecordDesc(recordDef,
                        pbf, recordType);
                setRecordDesc(recordDesc);
            }
        }

        if (getRecordDesc() == null) {
            throw new AssertionError("recordDesc");
        }
    }

    private FixedLengthRecordDef recordDef() {
        if (getRecordDef() == null) {
            final FixedLengthRecordDef r = createRecordDef();
            setRecordDef(r);
        }
        return getRecordDef();
    }

    private FixedLengthRecordDef createRecordDef() {
        /*
         * アノテーションから作成する
         */
        final FixedLengthRecordDef recordDef = createRecordDefByAnnotation();
        if (recordDef == null) {
            throw new AssertionError("recordDef");
        }
        return recordDef;
    }

    private FixedLengthRecordDef createRecordDefByAnnotation() {
        final DefaultFixedLengthRecordDef recordDef = new DefaultFixedLengthRecordDef();
        final List<PropertyDesc<BEAN>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<BEAN> pd : pds) {
            final FixedLengthColumn column = Annotations.getAnnotation(pd,
                    FixedLengthColumn.class);
            if (column == null) {
                continue;
            }
            final DefaultFixedLengthColumnDef columnDef = new DefaultFixedLengthColumnDef();
            columnDef.setPropertyName(pd.getPropertyName());
            columnDef.setBeginIndex(column.beginIndex());
            columnDef.setEndIndex(column.endIndex());
            recordDef.addColumnDef(columnDef);
        }
        if (recordDef.isEmpty()) {
            return null;
        }

        return recordDef;
    }

}
