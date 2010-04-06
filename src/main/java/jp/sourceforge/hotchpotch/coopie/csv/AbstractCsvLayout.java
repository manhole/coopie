package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import org.t2framework.commons.util.CollectionsUtil;

public abstract class AbstractCsvLayout<T> implements CsvLayout<T> {

    protected ColumnDesc<T>[] columnDescs;
    // 一時的
    protected ColumnNames columnNames;
    private boolean withHeader = true;
    protected OrderSpecified orderSpecified;

    public OrderSpecified getOrderSpecified() {
        if (orderSpecified == null) {
            getColumnDescs();
        }
        return orderSpecified;
    }

    public ColumnName[] getNames() {
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

    public void setValues(final T bean, final String[] values) {
        final ColumnDesc<T>[] cds = getColumnDescs();
        for (int i = 0; i < values.length; i++) {
            final String value = values[i];
            final ColumnDesc<T> cd = cds[i];
            cd.setValue(bean, value);
        }
    }

    protected abstract ColumnDesc<T>[] getColumnDescs();

    public void setupColumns(final ColumnSetupBlock block) {
        final DefaultColumnSetup columnSetup = new DefaultColumnSetup();
        block.setup(columnSetup);
        final ColumnNames columns = columnSetup.toColumnNames();
        setColumns(columns);
    }

    protected void setColumns(final String... names) {
        final ColumnNames columnNames = new ColumnNames();
        for (final String name : names) {
            final ColumnName columnName = new SimpleColumnName(name);
            columnNames.add(columnName);
        }
        setColumns(columnNames);
    }

    protected void setColumns(final ColumnName... names) {
        final ColumnNames columnNames = new ColumnNames();
        columnNames.addAll(names);
        setColumns(columnNames);
    }

    public void setColumns(final ColumnNames columnNames) {
        this.columnNames = columnNames;
        columnDescs = null;
    }

    /*
     * CSVを読むとき
     */
    public CsvLayout<T> setupByHeader(final String[] header) {
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

    @SuppressWarnings("unchecked")
    protected ColumnDesc<T>[] newColumnDescs(final int length) {
        return new ColumnDesc[length];
    }

    public boolean isWithHeader() {
        return withHeader;
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

}
