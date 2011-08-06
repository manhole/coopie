package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractLayout<T> {

    private static final Logger logger = LoggerFactory.getLogger();

    @SuppressWarnings("unchecked")
    protected static <U> ColumnDesc<U>[] newColumnDescs(final int length) {
        return new ColumnDesc[length];
    }

    protected static class DefaultRecordDesc<T> implements RecordDesc<T> {

        protected ColumnDesc<T>[] columnDescs;
        protected OrderSpecified orderSpecified;
        private final RecordType<T> recordType;

        protected DefaultRecordDesc(final ColumnDesc<T>[] columnDescs,
                final OrderSpecified orderSpecified,
                final RecordType<T> recordType) {
            this.columnDescs = columnDescs;
            this.orderSpecified = orderSpecified;
            this.recordType = recordType;
        }

        protected ColumnDesc<T>[] getColumnDescs() {
            return columnDescs;
        }

        @Override
        public String[] getHeaderValues() {
            final ColumnDesc<T>[] cds = getColumnDescs();
            if (cds == null) {
                return null;
            }
            final String[] line = new String[cds.length];
            for (int i = 0; i < cds.length; i++) {
                final ColumnDesc<T> cd = cds[i];
                final ColumnName cn = cd.getName();
                final String label = cn.getLabel();
                line[i] = label;
            }
            return line;
        }

        @Override
        public OrderSpecified getOrderSpecified() {
            return orderSpecified;
        }

        @Override
        public String[] getValues(final T bean) {
            final ColumnDesc<T>[] cds = getColumnDescs();
            final String[] values = new String[cds.length];
            for (int i = 0; i < cds.length; i++) {
                final ColumnDesc<T> cd = cds[i];
                final String value = cd.getValue(bean);
                values[i] = value(value);
            }
            return values;
        }

        @Override
        public void setValues(final T bean, final String[] values) {
            final ColumnDesc<T>[] cds = getColumnDescs();
            int i = 0;
            for (; i < values.length; i++) {
                final String value = value(values[i]);
                final ColumnDesc<T> cd = cds[i];
                cd.setValue(bean, value);
            }
            for (; i < cds.length; i++) {
                final String value = null;
                final ColumnDesc<T> cd = cds[i];
                cd.setValue(bean, value);
            }
        }

        /*
         * ""はnullと見なす。
         */
        private String value(final String v) {
            if (v == null || v.isEmpty()) {
                return null;
            }
            return v;
        }

        /*
         * CSVを読むとき
         */
        @Override
        public RecordDesc<T> setupByHeader(final String[] header) {
            /*
             * ColumnDescをヘッダの順序に合わせてソートし直す。
             */
            final ColumnDesc<T>[] tmpCds = getColumnDescs();
            final ColumnDesc<T>[] cds = newColumnDescs(header.length);

            int i = 0;
            HEADER: for (final String headerElem : header) {
                for (final ColumnDesc<T> cd : tmpCds) {
                    final ColumnName name = cd.getName();
                    if (name.getLabel().equals(headerElem)) {
                        cds[i] = cd;
                        i++;
                        continue HEADER;
                    }
                }
                //throw new RuntimeException("headerElem=" + headerElem);
                logger.debug("ignore column=[{}]", headerElem);
                cds[i] = new IgnoreColumnDesc<T>();
                i++;
            }
            columnDescs = cds;
            return this;
        }

        /*
         * CSVを書くとき。
         * Map Writerのときに使われる。
         */
        @Override
        public RecordDesc<T> setupByBean(final T bean) {
            return this;
        }

        @Override
        public T newInstance() {
            return recordType.newInstance();
        }

    }

    protected static interface RecordDescSetup<T> extends ColumnSetup {

        RecordDesc<T> getRecordDesc();

    }

    protected static abstract class AbstractColumnSetup<T> implements
            RecordDescSetup<T> {

        protected final List<ColumnName> columnNames = CollectionsUtil
                .newArrayList();

        @Override
        public void column(final ColumnName name) {
            columnNames.add(name);
        }

        @Override
        public void column(final String name) {
            column(new SimpleColumnName(name));
        }

        @Override
        public void column(final String propertyName, final String label) {
            final SimpleColumnName n = new SimpleColumnName();
            n.setName(propertyName);
            n.setLabel(label);
            column(n);
        }

    }

    protected static interface RecordType<T> {
        T newInstance();
    }

    protected static class BeanRecordType<T> implements RecordType<T> {

        private final BeanDesc<T> beanDesc;

        public BeanRecordType(final BeanDesc<T> beanDesc) {
            this.beanDesc = beanDesc;
        }

        @Override
        public T newInstance() {
            return beanDesc.newInstance();
        }

    }

    private static class IgnoreColumnDesc<T> implements ColumnDesc<T> {

        @Override
        public ColumnName getName() {
            return null;
        }

        @Override
        public String getValue(final T bean) {
            return null;
        }

        @Override
        public void setValue(final T bean, final String value) {
        }

    }

}
