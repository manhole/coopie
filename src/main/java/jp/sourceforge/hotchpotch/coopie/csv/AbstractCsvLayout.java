package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import org.t2framework.commons.util.CollectionsUtil;

public abstract class AbstractCsvLayout<T> implements CsvLayout<T> {

    protected ColumnNames columnNames;
    protected boolean withHeader = true;

    protected abstract RecordDesc<T> buildRecordDesc();

    public void setupColumns(final ColumnSetupBlock block) {
        final DefaultColumnSetup columnSetup = new DefaultColumnSetup();
        block.setup(columnSetup);
        final ColumnNames columns = columnSetup.toColumnNames();
        setColumns(columns);
    }

    public void setColumns(final ColumnNames columnNames) {
        this.columnNames = columnNames;
    }

    @SuppressWarnings("unchecked")
    protected static <U> ColumnDesc<U>[] newColumnDescs(final int length) {
        return new ColumnDesc[length];
    }

    public void setWithHeader(final boolean withHeader) {
        this.withHeader = withHeader;
    }

    private static class DefaultColumnSetup implements ColumnSetup {

        final List<ColumnName> columnNames = CollectionsUtil.newArrayList();

        public void column(final ColumnName name) {
            columnNames.add(name);
        }

        public void column(final String name) {
            column(new SimpleColumnName(name));
        }

        public void column(final String propertyName, final String label) {
            final SimpleColumnName n = new SimpleColumnName();
            n.setName(propertyName);
            n.setLabel(label);
            column(n);
        }

        public ColumnNames toColumnNames() {
            final ColumnNames n = new ColumnNames();
            n.addAll(columnNames);
            return n;
        }

    }

    protected static class DefaultRecordDesc<T> implements RecordDesc<T> {

        protected ColumnDesc<T>[] columnDescs;
        protected OrderSpecified orderSpecified;
        protected boolean withHeader;

        protected DefaultRecordDesc(final ColumnDesc<T>[] columnDescs,
            final OrderSpecified orderSpecified, final boolean withHeader) {
            this.columnDescs = columnDescs;
            this.orderSpecified = orderSpecified;
            this.withHeader = withHeader;
        }

        protected ColumnDesc<T>[] getColumnDescs() {
            return columnDescs;
        }

        @Override
        public ColumnName[] getColumnNames() {
            final ColumnDesc<T>[] cds = getColumnDescs();
            if (cds == null) {
                return null;
            }
            final ColumnName[] names = new ColumnName[cds.length];
            for (int i = 0; i < cds.length; i++) {
                final ColumnDesc<T> cd = cds[i];
                names[i] = cd.getName();
            }
            return names;
        }

        @Override
        public OrderSpecified getOrderSpecified() {
            if (orderSpecified == null) {
                getColumnDescs();
            }
            return orderSpecified;
        }

        @Override
        public String[] getValues(final T bean) {
            final ColumnDesc<T>[] cds = getColumnDescs();
            final String[] values = new String[cds.length];
            for (int i = 0; i < cds.length; i++) {
                final ColumnDesc<T> cd = cds[i];
                final String value = cd.getValue(bean);
                values[i] = value;
            }
            return values;
        }

        @Override
        public void setValues(final T bean, final String[] values) {
            final ColumnDesc<T>[] cds = getColumnDescs();
            for (int i = 0; i < values.length; i++) {
                final String value = values[i];
                final ColumnDesc<T> cd = cds[i];
                cd.setValue(bean, value);
            }
        }

        @Override
        public boolean isWithHeader() {
            return withHeader;
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
            final ColumnDesc<T>[] cds = newColumnDescs(tmpCds.length);

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
                // TODO
                throw new RuntimeException("headerElem=" + headerElem);
            }
            columnDescs = cds;
            return this;
        }

        /*
         * CSVを書くとき。
         * Map Writerのときに使われる。
         */
        @Override
        public RecordDesc setupByBean(final T bean) {
            return this;
        }

    }

}
