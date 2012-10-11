package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.Collections;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractBeanCsvLayout.CompositColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.AbstractBeanCsvLayout.DefaultColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.Annotations;
import jp.sourceforge.hotchpotch.coopie.csv.BeanPropertyBinding;
import jp.sourceforge.hotchpotch.coopie.csv.BeanRecordType;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnDescs;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnName;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.PropertyBinding;
import jp.sourceforge.hotchpotch.coopie.csv.PropertyBindingFactory;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordInOut;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
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

        prepareOpen();
        final RecordDesc<T> rd = getRecordDesc();
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

        prepareOpen();
        final RecordDesc<T> rd = getRecordDesc();
        final DefaultRecordWriter<T> w = new DefaultRecordWriter<T>(rd);
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
                final FixedLengthElementDesc[] elementDescs = setupElementDescs(recordDef);
                setFixedLengthElementDescs(elementDescs);
            }
            {
                final PropertyBindingFactory<T> pbf = new BeanPropertyBinding.Factory<T>(
                        beanDesc_);
                final ColumnDesc<T>[] cds = recordDefToColumnDesc(recordDef,
                        pbf);
                final RecordDesc<T> recordDesc = new FixedLengthRecordDesc<T>(
                        cds, new BeanRecordType<T>(beanDesc_));
                setRecordDesc(recordDesc);
            }
        }

        if (getRecordDesc() == null) {
            throw new IllegalStateException("recordDesc");
        }
    }

    static FixedLengthElementDesc[] setupElementDescs(
            final FixedLengthRecordDef recordDef) {
        final List<FixedLengthElementDesc> elementDescs = CollectionsUtil
                .newArrayList();
        for (final FixedLengthColumnDef columnDef : recordDef
                .getAllColumnDefs()) {
            final FixedLengthElementDesc elementDesc = new SimpleFixedLengthElementDesc(
                    columnDef.getBeginIndex(), columnDef.getEndIndex());
            elementDescs.add(elementDesc);
        }
        final FixedLengthElementDesc[] descs = elementDescs
                .toArray(new FixedLengthElementDesc[elementDescs.size()]);
        return descs;
    }

    static <T> ColumnDesc<T>[] recordDefToColumnDesc(
            final FixedLengthRecordDef recordDef,
            final PropertyBindingFactory<T> pbf) {
        final List<ColumnDesc<T>> list = CollectionsUtil.newArrayList();
        appendColumnDescFromColumnDef(recordDef, list, pbf);
        appendColumnDescFromColumnsDef(recordDef, list, pbf);
        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);
        return cds;
    }

    private static <T> void appendColumnDescFromColumnDef(
            final FixedLengthRecordDef recordDef,
            final List<ColumnDesc<T>> list, final PropertyBindingFactory<T> pbf) {
        for (final FixedLengthColumnDef columnDef : recordDef.getColumnDefs()) {
            final ColumnName columnName = columnDef.getColumnName();
            final PropertyBinding<T, Object> pb = pbf
                    .getPropertyBinding(columnDef.getPropertyName());
            final ColumnDesc<T> cd = DefaultColumnDesc.newColumnDesc(
                    columnName, pb, columnDef.getConverter());
            list.add(cd);
        }
    }

    private static <T> void appendColumnDescFromColumnsDef(
            final FixedLengthRecordDef recordDef,
            final List<ColumnDesc<T>> list, final PropertyBindingFactory<T> pbf) {
        for (final FixedLengthColumnsDef columnsDef : recordDef
                .getColumnsDefs()) {
            final List<ColumnName> columnNames = CollectionsUtil.newArrayList();
            for (final FixedLengthColumnDef columnDef : columnsDef
                    .getColumnDefs()) {
                columnNames.add(columnDef.getColumnName());
            }
            final PropertyBinding<T, Object> pb = pbf
                    .getPropertyBinding(columnsDef.getPropertyName());
            final ColumnDesc<T>[] cds = CompositColumnDesc
                    .newCompositColumnDesc(columnNames, pb,
                            columnsDef.getConverter());
            Collections.addAll(list, cds);
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
        final List<PropertyDesc<T>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<T> pd : pds) {
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

        // TODO CSV側へやり方を合わせる
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

}
