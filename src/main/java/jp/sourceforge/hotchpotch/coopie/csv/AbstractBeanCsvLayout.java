package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public abstract class AbstractBeanCsvLayout<BEAN> extends
        AbstractCsvLayout<BEAN> {

    private final BeanDesc<BEAN> beanDesc_;

    private CsvRecordDefCustomizer customizer_ = EmptyRecordDefCustomizer
            .getInstance();

    public AbstractBeanCsvLayout(final Class<BEAN> beanClass) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass);
    }

    protected void prepareOpen() {
        if (getRecordDesc() == null) {
            final CsvRecordDef recordDef = recordDef();
            customizer_.customize(recordDef);
            final RecordDesc<BEAN> recordDesc = createRecordDesc(recordDef);
            setRecordDesc(recordDesc);
        }

        if (getRecordDesc() == null) {
            throw new AssertionError("recordDesc");
        }
    }

    private RecordDesc<BEAN> createRecordDesc(final CsvRecordDef recordDef) {
        final PropertyBindingFactory<BEAN> pbf = new BeanPropertyBinding.Factory<BEAN>(
                beanDesc_);
        final BeanRecordType<BEAN> recordType = new BeanRecordType<BEAN>(
                beanDesc_);
        // TODO アノテーションのorderが全て指定されていた場合はSPECIFIEDにするべきでは?
        final RecordDesc<BEAN> recordDesc = createRecordDesc(recordDef, pbf,
                recordType);
        return recordDesc;
    }

    private CsvRecordDef recordDef() {
        if (getRecordDef() == null) {
            final CsvRecordDef r = createRecordDef();
            setRecordDef(r);
        }
        return getRecordDef();
    }

    private CsvRecordDef createRecordDef() {
        /*
         * アノテーションが付いている場合は、アノテーションを優先する
         */
        CsvRecordDef recordDef = createRecordDefByAnnotation();
        if (recordDef == null) {
            /*
             * beanの全プロパティを対象に。
             */
            recordDef = createRecordDefByProperties();
        }
        if (recordDef == null) {
            throw new AssertionError("recordDef");
        }
        return recordDef;
    }

    private CsvRecordDef createRecordDefByAnnotation() {
        final DefaultCsvRecordDef recordDef = new DefaultCsvRecordDef();
        final List<PropertyDesc<BEAN>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<BEAN> pd : pds) {
            final CsvColumns columns = Annotations.getAnnotation(pd,
                    CsvColumns.class);
            if (columns != null) {
                final DefaultCsvColumnsDef columnsDef = new DefaultCsvColumnsDef();
                columnsDef.setup(columns, pd);
                recordDef.addColumnsDef(columnsDef);
                continue;
            }
            // TODO: CsvColumnとCsvColumnsの両方があったら例外にすること

            final CsvColumn column = Annotations.getAnnotation(pd,
                    CsvColumn.class);
            if (column != null) {
                final DefaultCsvColumnDef columnDef = new DefaultCsvColumnDef();
                columnDef.setup(column, pd);
                recordDef.addColumnDef(columnDef);
            }
        }

        if (recordDef.isEmpty()) {
            return null;
        }

        Collections.sort(recordDef.getColumnDefs(),
                CsvColumnDefComparator.getInstance());
        return recordDef;
    }

    private CsvRecordDef createRecordDefByProperties() {
        final DefaultCsvRecordDef recordDef = new DefaultCsvRecordDef();
        final List<PropertyDesc<BEAN>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<BEAN> pd : pds) {
            final DefaultCsvColumnDef columnDef = new DefaultCsvColumnDef();
            // orderは未指定とする
            columnDef.setup(pd);
            recordDef.addColumnDef(columnDef);
        }
        return recordDef;
    }

    public void setCustomizer(final CsvRecordDefCustomizer columnCustomizer) {
        customizer_ = columnCustomizer;
    }

    static class CsvColumnDefComparator implements Comparator<CsvColumnDef> {

        private static CsvColumnDefComparator INSTANCE = new CsvColumnDefComparator();

        public static CsvColumnDefComparator getInstance() {
            return INSTANCE;
        }

        @Override
        public int compare(final CsvColumnDef o1, final CsvColumnDef o2) {
            // orderが小さい方を左側に
            final int ret = o1.getOrder() - o2.getOrder();
            return ret;
        }

    }

    static class PropertyNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public PropertyNotFoundException(final String message) {
            super(message);
        }

    }

}
